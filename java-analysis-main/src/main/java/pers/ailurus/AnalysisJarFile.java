package pers.ailurus;

import pers.ailurus.model.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AnalysisJarFile {
    public static void main(String[] args) {
        // java -jar AnalysisJarFile.jar path_to_jar_file.jar
//        String jarFilePath = args[0];

        String testSet = "C:\\Users\\wzy\\Desktop\\test";
        String testSetCSV = "C:\\Users\\wzy\\Desktop\\test\\test.csv";
        try {
            DataOperator.initOperator();
            List<String[]> info = FileUtil.readCSVFile(testSetCSV);
            for (String[] maven: info) {
                if ("file_name".equals(maven[2])) continue;
                System.out.println(testSet + File.separator + maven[2]);
                List<Result> result = Comparator.deduceTPL(testSet + File.separator + maven[2]);
                System.out.println(result);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Run implements Runnable {

        @Override
        public void run() {
        }
    }
}
