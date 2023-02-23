package pers.ailurus.utils;

import pers.ailurus.models.feature.CDG;
import pers.ailurus.models.feature.FeatureClass;
import pers.ailurus.models.feature.FeaturePackage;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Analysis {

    public static void initSoot(String path) {
        G.reset();
        Options.v().set_whole_program(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(List.of(path));
        Scene.v().loadNecessaryClasses();
    }

    public static FeaturePackage analysisPackage(String jarPath, String groupId, String artifactId, String version) {
        initSoot(jarPath);

        // 获取类依赖图
        Chain<SootClass> appClassChain = Scene.v().getApplicationClasses();
        Chain<SootClass> libClassChain = Scene.v().getLibraryClasses();
        Chain<SootClass> phantomClassChain = Scene.v().getPhantomClasses();
        List<SootClass> appClassList = new ArrayList<>(appClassChain);
        List<SootClass> libClassList = new ArrayList<>(libClassChain);
        List<SootClass> phantomClassList = new ArrayList<>(phantomClassChain);

        CDG cdg = new CDG(appClassList, libClassList, phantomClassList);

        FeaturePackage featurePackage = new FeaturePackage(groupId, artifactId, version);
        featurePackage.analysisPackage(appClassList, cdg);

        return featurePackage;
    }


    public static int getTypeId(String clazz, Map<String, Integer> classMap) {
        int id = classMap.getOrDefault(clazz, -1);
        if (id == -1) {
            int size = classMap.size();
            int count = (clazz.length() - clazz.indexOf("[]"))/2;
            return size * count + classMap.get(clazz.substring(0, clazz.indexOf("[]")));
        }
        return id;
    }
}
