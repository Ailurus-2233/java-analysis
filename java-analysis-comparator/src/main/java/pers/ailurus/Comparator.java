package pers.ailurus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.lang.Console;
import pers.ailurus.model.AnalysisClass;
import pers.ailurus.model.AnalysisMethod;
import pers.ailurus.model.AnalysisPackage;
import pers.ailurus.model.FeatureClass;
import pers.ailurus.model.FeatureFile;
import pers.ailurus.model.Result;

public class Comparator {
    public static List<Result> deduceTPL(String jarFilePath) {
        // 解析jar文件
        AnalysisPackage ap = Extractor.extract(jarFilePath);

        // 查询类似的TPL
        List<String> ffList = DataOperator.selectFileMd5ByNumberFeature(new FeatureFile(
                null, null, null, null, ap.getClassNum(), ap.getPackageDeep(), ap.getPackageNum()));
        Console.print("查询到 {} 个类似的TPL", ffList.size());

        Map<String, Integer> ffMap = new HashMap<>();

        for (AnalysisClass ac : ap.getClasses()) {
            List<String> fcList = DataOperator.selectFeatureClassByNumberFeature(new FeatureClass(
                    null, ac.getModifier(), ac.getInterfaceNum(), ac.getIsHasSuperClass(),
                    ac.getFieldNum(), ac.getMethodNum(), ac.getNumOfDep(), ac.getNumOfBeDep()), ffList);
            Console.print("查询到 {} 个类似的Class", fcList.size());
            Map<String, Integer> fcMap = new HashMap<>();
            for (AnalysisMethod am : ac.getMethods()) {
                List<String> classMd5List = DataOperator.selectClassMd5ByMethodMd5(am.getMd5());
                for (String classMd5 : classMd5List) {
                    boolean flag = false;
                    for (String fc : fcList) {
                        if (fc.equals(classMd5)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        fcMap.put(classMd5, fcMap.getOrDefault(classMd5, 0) + 1);
                    }
                }
            }
            // 判断该class 最可能是数据库中的哪一个class文件
            int max = 0;
            for (Map.Entry<String, Integer> entry : fcMap.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                }
            }
            for (Map.Entry<String, Integer> entry : fcMap.entrySet()) {
                if (entry.getValue() == max) {
                    boolean flag = false;
                    List<String> fileMd5List = DataOperator.selectFileMd5ByClassMd5(entry.getKey());
                    for (String fileMd5 : fileMd5List) {
                        for (String ff : ffList) {
                            if (ff.equals(fileMd5)) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            ffMap.put(fileMd5, ffMap.getOrDefault(fileMd5, 0) + 1 + ac.getNumOfDep() + ac.getNumOfBeDep());
                        }
                    }
                }
            }
        }

        // 文件选择
        int max = 0;
        for (Map.Entry<String, Integer> entry : ffMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }

        List<Result> ans = new ArrayList<>();
        for (String ff : ffList) {
            if (ffMap.getOrDefault(ff, 0) == max) {
                FeatureFile temp = DataOperator.selectFeatureFileByMd5(ff);
                ans.add(new Result(temp.getGroupId(), temp.getArtifactId(), temp.getVersion()));
            }
        }
        return ans;
    }

    public static List<Result> deduceTPLByFileMd5(String md5) {
        FeatureFile ff = DataOperator.selectFeatureFileByMd5(md5);
        List<FeatureClass> classList = DataOperator.selectFeatureClassByFileMd5(md5);

        List<FeatureFile> ffList = DataOperator.selectFeatureFileByNumberFeature(new FeatureFile(
                null, null, null, null, ff.getClassNum(), ff.getPackageDeep(), ff.getPackageNum()));
        Map<String, Integer> ffMap = new HashMap<>();

        for (FeatureClass fc : classList) {
            List<String> methodList = DataOperator.selectMethodMd5ByClassMd5(fc.getMd5());
            List<FeatureClass> fcList = DataOperator.selectFeatureClassByNumberFeature(new FeatureClass(
                    null, fc.getModifier(), fc.getInterfaceNum(), fc.getHasSuperClass(),
                    fc.getFieldNum(), fc.getMethodNum(), fc.getDepClassNum(), fc.getBeDepNum()));
            Map<String, Integer> fcMap = new HashMap<>();
            for (String methodMd5 : methodList) {
                List<String> classMd5List = DataOperator.selectClassMd5ByMethodMd5(methodMd5);
                for (String classMd5 : classMd5List) {
                    boolean flag = false;
                    for (FeatureClass t : fcList) {
                        if (t.getMd5().equals(classMd5)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        fcMap.put(classMd5, fcMap.getOrDefault(classMd5, 0) + 1);
                    }
                }
            }
            // 判断该class 最可能是数据库中的哪一个class文件
            int max = 0;
            for (Map.Entry<String, Integer> entry : fcMap.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                }
            }
            for (Map.Entry<String, Integer> entry : fcMap.entrySet()) {
                if (entry.getValue() == max) {
                    boolean flag = false;
                    List<String> fileMd5List = DataOperator.selectFileMd5ByClassMd5(entry.getKey());
                    for (String fileMd5 : fileMd5List) {
                        for (FeatureFile t : ffList) {
                            if (t.getMd5().equals(fileMd5)) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            ffMap.put(fileMd5, ffMap.getOrDefault(fileMd5, 0) + 1 + fc.getDepClassNum() + fc.getBeDepNum());
                        }
                    }
                }
            }
        }
        // 文件选择
        int max = 0;
        for (Map.Entry<String, Integer> entry : ffMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }

        List<Result> ans = new ArrayList<>();
        for (FeatureFile t : ffList) {
            if (ffMap.getOrDefault(t.getMd5(), 0) == max) {
                ans.add(new Result(t.getGroupId(), t.getArtifactId(), t.getVersion()));
            }
        }
        return ans;
    }
}
