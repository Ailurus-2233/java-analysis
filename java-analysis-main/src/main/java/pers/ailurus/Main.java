package pers.ailurus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.ailurus.model.AnalysisPackage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static pers.ailurus.Extractor.extract;
import static pers.ailurus.FileUtil.*;
import static pers.ailurus.NetUtil.dowlnoad;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        String maven_path = args[0];
        try {
            List<String[]> info = readCSVFile(maven_path);
            long sumTime = 0;
            for (int i = 1; i < info.size(); i++) {
                long startTime = System.currentTimeMillis();
                String[] line = info.get(i);
                String name = line[0];
                String version = line[1];
                String url = line[2];
                String path = "jar" + File.separator + name + "-" + version + ".jar";
                dowlnoad(url, name + "-" + version + ".jar", "jar" + File.separator);
                MPDataOperator.insert(ObjectGenerator.getMavenRepository(name, version, url, path));
                AnalysisPackage ap = extract(path);
                MPDataOperator.insertAnalysisPackage(name, version, ap);
                deleteFile(path);
                deleteFolder("jar"+ File.separator + name + "-" + version);
                long endTime = System.currentTimeMillis();
                sumTime += endTime - startTime;
                logger.info(name + "-" + version + ".jar 分析完成，耗时" + (endTime - startTime) + "ms");
            }

            logger.info("总分析数量：" + info.size());
            logger.info("总耗时：" + sumTime + "ms");
            logger.info("平均耗时：" + sumTime / info.size() + "ms");
            MPDataOperator.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}