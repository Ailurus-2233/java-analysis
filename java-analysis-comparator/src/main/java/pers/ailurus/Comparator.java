package pers.ailurus;

import java.io.IOException;
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
    public Result deduceTPL(String jarFilePath) {
        // 解析jar文件
        AnalysisPackage ap = Extractor.extract(jarFilePath);
        
        // 查询类似的TPL
        try {
            DataOperator.initOperator();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<FeatureFile> ffList = DataOperator.selectFeatureFileByNumberFeature(new FeatureFile(null, null, null, ap.getClassNum(), ap.getPackageDeep(), ap.getPackageNum()));

        Map<String, Integer> ffMap = new HashMap<>();

        for (FeatureFile ff : ffList) {
            ffMap.put(ff.getMd5(), 0);
        }

        for (AnalysisClass ac : ap.getClasses()) {
            List<FeatureClass> fcList = DataOperator.selectFeatureClassByNumberFeature(new FeatureClass(null, ac.getModifier(), ac.getInterfaceNum(), ac.getIsHasSuperClass(), ac.getFieldNum(), ac.getMethodNum(), ac.getNumOfDep(), ac.getNumOfBeDep()));
            Map<String, Integer> fcMap = new HashMap<>();
            for (AnalysisMethod am : ac.getMethods()) {
                List<String> classMd5List = DataOperator.selectClassMd5ByMethodMd5(am.getMd5());
                for (String classMd5 : classMd5List) {
                    fcMap.put(classMd5, fcMap.getOrDefault(classMd5, 0) + 1);
                }
            }
        }
        return null;
    }
}
