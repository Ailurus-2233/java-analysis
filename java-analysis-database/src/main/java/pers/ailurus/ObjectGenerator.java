package pers.ailurus;

import pers.ailurus.model.*;

import java.io.File;
import java.io.IOException;

public class ObjectGenerator {

    public static FeatureFile getFileFeatureByAnalysisPackage(String groupId, String artifactId, String version, AnalysisPackage ap) {
        FeatureFile featureFile = new FeatureFile();
        featureFile.setGroupId(groupId);
        featureFile.setArtifactId(artifactId);
        featureFile.setVersion(version);
        featureFile.setPackageNum(ap.getPackageNum());
        featureFile.setClassNum(ap.getClassNum());
        featureFile.setMd5(ap.getMd5());
        featureFile.setPackageDeep(ap.getPackageDeep());
        return featureFile;
    }

    public static FeatureClass getClassFeatureByAnalysisClass(AnalysisClass ac) {
        FeatureClass fc = new FeatureClass();
        fc.setMd5(ac.getMd5());
        fc.setBeDepNum(ac.getNumOfBeDep());
        fc.setDepClassNum(ac.getNumOfDep());
        fc.setFieldNum(ac.getFieldNum());
        fc.setModifier(ac.getModifier());
        fc.setInterfaceNum(ac.getInterfaceNum());
        fc.setMethodNum(ac.getMethodNum());
        fc.setHasSuperClass(ac.getIsHasSuperClass());
        return fc;
    }

    public static FeatureMethod getMethodFeatureByAnalysisMethod(AnalysisMethod am) {
        FeatureMethod fm = new FeatureMethod();
        fm.setMd5(am.getMd5());
        fm.setModifier(am.getModifier());
        fm.setArgsNum(am.getArgsNum());
        fm.setCfgFinger(am.getCfgFinger());
        fm.setReturnType(am.getReturnType());
        return fm;
    }

    public static MavenRepository getMavenRepository(String groupId, String artifactId, String version, String url, String filePath) {
        MavenRepository mr = new MavenRepository();
        mr.setGroupId(groupId);
        mr.setArtifactId(artifactId);
        mr.setVersion(version);
        mr.setUrl(url);
        mr.setSize(new File(filePath).length());
        return mr;
    }

}
