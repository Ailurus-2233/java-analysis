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
        int ttCount = 0; // 名称版本都正确的数量
        int tfCount = 0; // 只有名称正确的数量
        int ffCount = 0; // 名称错误的数量
        try {
            DataOperator.initOperator();
            List<String[]> info = FileUtil.readCSVFile(testSetCSV);
            for (String[] maven: info) {
                if ("file_name".equals(maven[2])) continue;
                System.out.println(testSet + File.separator + maven[2]);
                Result result = Comparator.deduceTPL(testSet + File.separator + maven[2]);
                System.out.println(result);
                if (maven[3].equals(result.getName()) && maven[4].equals(result.getVersion())) {
                    ttCount ++;
                } else if (maven[3].equals(result.getName())) {
                    tfCount ++;
                } else {
                    ffCount ++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("预测正确数量: %d, 预测名称正确数量: %d, 预测错误数量: %d", ttCount, tfCount, ffCount);
    }

    class Run implements Runnable {

        @Override
        public void run() {
        }
    }
}
