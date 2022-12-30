package pers.ailurus;

import pers.ailurus.model.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static pers.ailurus.ObjectGenerator.*;

public class CSVOperator {

    private static File ffCSV;
    private static File fcCSV;
    private static File fmCSV;
    private static File rfcCSV;
    private static File rcmCSV;
    private static File mrCSV;


    public static void initCSVFile(String savePath) throws IOException {

        File path = new File(savePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        ffCSV = new File(String.format("%s%s%s", path, File.separator, "feature_file.csv"));
        fcCSV = new File(String.format("%s%s%s", path, File.separator, "feature_class.csv"));
        fmCSV = new File(String.format("%s%s%s", path, File.separator, "feature_method.csv"));
        rfcCSV = new File(String.format("%s%s%s", path, File.separator, "relation_file_class.csv"));
        rcmCSV = new File(String.format("%s%s%s", path, File.separator, "relation_class_method.csv"));
        mrCSV = new File(String.format("%s%s%s", path, File.separator, "maven_repository.csv"));


        if (!ffCSV.exists() && ffCSV.createNewFile()) {
            MyFileUtil.writeLine("md5,group_id,artifact_id,version,class_num,package_deep,package_num", ffCSV.getAbsolutePath());
        }
        if (!fcCSV.exists() && fcCSV.createNewFile()) {
            MyFileUtil.writeLine("md5,modifier,interface_num,has_super_class,field_num,method_num,dep_class_num,be_dep_num", fcCSV.getAbsolutePath());
        }
        if (!fmCSV.exists() && fmCSV.createNewFile()) {
            MyFileUtil.writeLine("md5,modifier,args_num,return_type, cfg_finger", fmCSV.getAbsolutePath());
        }
        if (!rfcCSV.exists() && rfcCSV.createNewFile()) {
            MyFileUtil.writeLine("file_md5,class_md5", rfcCSV.getAbsolutePath());
        }
        if (!rcmCSV.exists() && rcmCSV.createNewFile()) {
            MyFileUtil.writeLine("class_md5,method_md5", rcmCSV.getAbsolutePath());
        }
        if (!mrCSV.exists() && mrCSV.createNewFile()) {
            MyFileUtil.writeLine("group_id,artifact_id,version,url,size", mrCSV.getAbsolutePath());
        }
    }

    public static void save(FeatureFile ff) {
        MyFileUtil.writeLine(ff.toCSVLine(), ffCSV.getAbsolutePath());
    }

    public static void save(FeatureClass fc) {
        MyFileUtil.writeLine(fc.toCSVLine(), fcCSV.getAbsolutePath());
    }

    public static void save(FeatureMethod fm) {
        MyFileUtil.writeLine(fm.toCSVLine(), fmCSV.getAbsolutePath());
    }

    public static void save(RelationFileClass rfc) {
        MyFileUtil.writeLine(rfc.toCSVLine(), rfcCSV.getAbsolutePath());
    }

    public static void save(RelationClassMethod rcm) {
        MyFileUtil.writeLine(rcm.toCSVLine(), rcmCSV.getAbsolutePath());
    }

    public static void save(MavenRepository mr) {
        MyFileUtil.writeLine(mr.toCSVLine(), mrCSV.getAbsolutePath());
    }

    public static void saveAnalysisPackage(String groupId, String artifactId, String version, AnalysisPackage ap) {
        FeatureFile featureFile = getFileFeatureByAnalysisPackage(groupId, artifactId, version, ap);
        Set<String> fcSet = new HashSet<>();
        Set<String> fmSet = new HashSet<>();
        Set<String> rcmSet = new HashSet<>();
        Set<String> rfcSet = new HashSet<>();
        for (AnalysisClass ac : ap.getClasses()) {
            FeatureClass fc = getClassFeatureByAnalysisClass(ac);
            fcSet.add(fc.toCSVLine());
            rfcSet.add(new RelationFileClass(featureFile.getMd5(), fc.getMd5()).toCSVLine());
            for (AnalysisMethod am : ac.getMethods()) {
                FeatureMethod fm = getMethodFeatureByAnalysisMethod(am);
                fmSet.add(fm.toCSVLine());
                rcmSet.add(new RelationClassMethod(fc.getMd5(), fm.getMd5()).toCSVLine());
            }
        }
        save(featureFile);
        MyFileUtil.writeLines(fcSet.stream().toList(), fcCSV.getAbsolutePath());
        MyFileUtil.writeLines(fmSet.stream().toList(), fmCSV.getAbsolutePath());
        MyFileUtil.writeLines(rfcSet.stream().toList(), rfcCSV.getAbsolutePath());
        MyFileUtil.writeLines(rcmSet.stream().toList(), rcmCSV.getAbsolutePath());
    }
}
