package pers.ailurus;

import pers.ailurus.model.AnalysisPackage;

public class AnalysisJarFile {
    public static void main(String[] args) {
        // java -jar AnalysisJarFile.jar path_to_jar_file.jar
        String jarFilePath = args[0];
        // 解析jar文件
        AnalysisPackage ap = Extractor.extract(jarFilePath);

        

    }
}
