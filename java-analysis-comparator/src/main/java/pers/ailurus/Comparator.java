package pers.ailurus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pers.ailurus.model.AnalysisClass;
import pers.ailurus.model.AnalysisMethod;
import pers.ailurus.model.AnalysisPackage;
import pers.ailurus.model.FeatureClass;
import pers.ailurus.model.FeatureFile;
import pers.ailurus.model.Result;

public class Comparator {
    public static Result deduceTPL(String jarFilePath) {
        // 解析jar文件
        AnalysisPackage ap = Extractor.extract(jarFilePath);

        // 查询类似的TPL
        List<FeatureFile> ffList = DataOperator.selectFeatureFileByNumberFeature(new FeatureFile(null, null, null, ap.getClassNum(), ap.getPackageDeep(), ap.getPackageNum()));

        Map<String, Integer> ffMap = new HashMap<>();


        for (AnalysisClass ac : ap.getClasses()) {
            List<FeatureClass> fcList = DataOperator.selectFeatureClassByNumberFeature(new FeatureClass(null, ac.getModifier(), ac.getInterfaceNum(), ac.getIsHasSuperClass(), ac.getFieldNum(), ac.getMethodNum(), ac.getNumOfDep(), ac.getNumOfBeDep()));
            Map<String, Integer> fcMap = new HashMap<>();
            for (AnalysisMethod am : ac.getMethods()) {
                List<String> classMd5List = DataOperator.selectClassMd5ByMethodMd5(am.getMd5());
                for (String classMd5 : classMd5List) {
                    boolean flag = false;
                    for (FeatureClass fc : fcList) {
                        if (fc.getMd5().equals(classMd5)) {
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
                        for (FeatureFile ff : ffList) {
                            if (ff.getMd5().equals(fileMd5)) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            ffMap.put(fileMd5, ffMap.getOrDefault(fileMd5, 0) + 1);
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

        for (FeatureFile ff : ffList) {
            if (ffMap.getOrDefault(ff.getMd5(), 0) == max) {
                return new Result(ff.getName(), ff.getVersion());
            }
        }
        return null;
    }
}
