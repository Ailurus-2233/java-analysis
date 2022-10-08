package pers.ailurus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.ailurus.model.AnalysisPackage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static pers.ailurus.Extractor.extract;
import static pers.ailurus.FileUtil.*;
import static pers.ailurus.NetUtil.download;

public class MavenMainWithMysql {

    private static Logger logger = LoggerFactory.getLogger(MavenMainWithMysql.class);

    public static void main(String[] args) {
        String maven_path = args[0];
        try {
            DataOperator.initOperator();
            List<String[]> info = readCSVFile(maven_path);
            long sumTime = 0;
            for (int i = 1; i < info.size(); i++) {
                long startTime = System.currentTimeMillis();
                String[] line = info.get(i);
                String name = line[0];
                String version = line[1];
                String url = line[2];
                String path = "jar" + File.separator + name + "-" + version + ".jar";
                String downloadName = name + "-" + version + ".jar";
                logger.info(downloadName + " 开始分析");
                download(url, downloadName, "jar" + File.separator, 0);
                long time1 = System.currentTimeMillis();
                logger.info(downloadName + " 下载耗时：" + (time1 - startTime) + "ms");
                DataOperator.insert(ObjectGenerator.getMavenRepository(name, version, url, path));
                AnalysisPackage ap = extract(path);
                long time2 = System.currentTimeMillis();
                logger.info(downloadName + " 分析耗时：" + (time2 - time1) + "ms");
                DataOperator.insertAnalysisPackage(name, version, ap);
                deleteFile(path);
                deleteFolder("jar" + File.separator + name + "-" + version);
                long endTime = System.currentTimeMillis();
                sumTime += endTime - startTime;
                logger.info(downloadName + " 数据插入耗时：" + (endTime - time2) + "ms");
                logger.info(downloadName + ".jar 分析完成，总耗时" + (endTime - startTime) + "ms");
            }

            logger.info("总分析数量：" + info.size());
            logger.info("总耗时：" + sumTime + "ms");
            logger.info("平均耗时：" + sumTime / info.size() + "ms");
            DataOperator.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}