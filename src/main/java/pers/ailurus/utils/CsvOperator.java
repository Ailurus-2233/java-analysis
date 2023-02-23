package pers.ailurus.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;

import java.io.File;
import java.util.ArrayList;

public class CsvOperator {

    private static String path;
    private static String dataPackagePath;
    private static ArrayList<String[]> dataPackages;
    private static String dataClassPath;
    private static ArrayList<String[]> dataClasses;
    private static String dataMethodPath;
    private static ArrayList<String[]> dataMethods;
    private static String dataBlockPath;
    private static ArrayList<String[]> dataBlocks;

    private static String relationPackageClassPath;
    private static ArrayList<String[]> relationPackageClasses;
    private static String relationClassMethodPath;
    private static ArrayList<String[]> relationClassMethods;
    private static String relationMethodBlockPath;
    private static ArrayList<String[]> relationMethodBlocks;

    public static void init(String path) {
        initDir(path);
        initFile();
        initData();

    }

    public static void init(String path, String model) {
        initDir(path);
        initFile(model);
        initData();
    }

    private static void initDir(String path) {
        CsvOperator.path = path;

        CsvOperator.dataPackagePath = path + File.separator + "dataPackage.csv";
        CsvOperator.dataClassPath = path + File.separator + "dataClass.csv";
        CsvOperator.dataMethodPath = path + File.separator + "dataMethod.csv";
        CsvOperator.dataBlockPath = path + File.separator + "dataBlock.csv";

        CsvOperator.relationPackageClassPath = path + File.separator + "relationPackageClass.csv";
        CsvOperator.relationClassMethodPath = path + File.separator + "relationClassMethod.csv";
        CsvOperator.relationMethodBlockPath = path + File.separator + "relationMethodBlock.csv";
    }

    public static void initFile() {
        if (!FileUtil.exist(path))
            FileUtil.mkdir(path);

        if (!FileUtil.exist(dataPackagePath))
            FileUtil.touch(dataPackagePath);
        if (!FileUtil.exist(dataClassPath))
            FileUtil.touch(dataClassPath);
        if (!FileUtil.exist(dataMethodPath))
            FileUtil.touch(dataMethodPath);
        if (!FileUtil.exist(dataBlockPath))
            FileUtil.touch(dataBlockPath);

        if (!FileUtil.exist(relationPackageClassPath))
            FileUtil.touch(relationPackageClassPath);
        if (!FileUtil.exist(relationClassMethodPath))
            FileUtil.touch(relationClassMethodPath);
        if (!FileUtil.exist(relationMethodBlockPath))
            FileUtil.touch(relationMethodBlockPath);
    }

    public static void initFile(String model) {
        if ("clear".equals(model)) {
            FileUtil.del(path);
        }
        initFile();
    }

    public static void initData() {
        dataPackages = new ArrayList<>();
        dataClasses = new ArrayList<>();
        dataMethods = new ArrayList<>();
        dataBlocks = new ArrayList<>();

        relationPackageClasses = new ArrayList<>();
        relationClassMethods = new ArrayList<>();
        relationMethodBlocks = new ArrayList<>();
    }


    public static void addRelationPackageClass(String from, String to) {
        relationPackageClasses.add(new String[]{from, to});
    }

    public static void addRelationClassMethod(String from, String to) {
        relationClassMethods.add(new String[]{from, to});
    }

    public static void addRelationMethodBlock(String from, String to) {
        relationMethodBlocks.add(new String[]{from, to});
    }

    public static void write() {
        if (dataPackages != null)
            getCsvWriter(dataPackagePath).write(dataPackages).writeLine().close();
        if (dataClasses != null)
            getCsvWriter(dataClassPath).write(dataClasses).writeLine().close();
        if (dataMethods != null)
            getCsvWriter(dataMethodPath).write(dataMethods).writeLine().close();
        if (dataBlocks != null)
            getCsvWriter(dataBlockPath).write(dataBlocks).writeLine().close();

        if (relationPackageClasses != null)
            getCsvWriter(relationPackageClassPath).write(relationPackageClasses).writeLine().close();
        if (relationClassMethods != null)
            getCsvWriter(relationClassMethodPath).write(relationClassMethods).writeLine().close();
        if (relationMethodBlocks != null)
            getCsvWriter(relationMethodBlockPath).write(relationMethodBlocks).writeLine().close();

        initData();
    }

    private static CsvWriter getCsvWriter(String path) {
        return new CsvWriter(path, CharsetUtil.CHARSET_UTF_8, true);
    }

}
