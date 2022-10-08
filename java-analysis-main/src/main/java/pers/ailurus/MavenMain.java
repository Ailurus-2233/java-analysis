package pers.ailurus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.ailurus.model.AnalysisPackage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static pers.ailurus.Extractor.extract;
import static pers.ailurus.FileUtil.*;
import static pers.ailurus.NetUtil.download;

public class MavenMain {
    private static Logger logger = LoggerFactory.getLogger(MavenMain.class);

    public static void main(String[] args) {
        String mavenPath = args[0];
        List<String[]> info = null;
        try {
            info = readCSVFile(mavenPath);
            long sumTime = 0;
            CSVOperator.initCSVFile(getFileNameWithOutSuffix(mavenPath));
            for (int i = 1; i < info.size(); i++) {
                long startTime = System.currentTimeMillis();
                String[] line = info.get(i);
                String name = line[0];
                String version = line[1];
                String url = line[2];
                String flag = line[3];
                String path = "jar" + File.separator + name + "-" + version + ".jar";
                String downloadName = name + "-" + version + ".jar";
                if ("1".equals(flag) || "-1".equals(flag) || "-2".equals(flag)) {
                    logger.info(String.format("%s 已分析", downloadName));
                    continue;
                }
                logger.info(downloadName + " 开始分析");
                // 使用国内源
                url = url.replace("https://repo1.maven.org/maven2/", "https://maven.aliyun.com/repository/public/");
                download(url, downloadName, "jar" + File.separator, 0);
                long time1 = System.currentTimeMillis();
                logger.info(downloadName + " 下载耗时：" + (time1 - startTime) + "ms");
                AnalysisPackage ap = null;
                try {
                    ap = extract(path, 60);
                } catch (TimeoutException e) {
                    logger.warn(String.format("%s 分析超时", downloadName));
                    line[3] = "-2";
                    deleteFile(path);
                    deleteFolder("jar" + File.separator + name + "-" + version);
                    FileUtil.writeCSV(info, mavenPath);
                    continue;
                } catch (Exception e) {
                    logger.warn(String.format("%s 分析异常", downloadName));
                    line[3] = "-1";
                    deleteFile(path);
                    deleteFolder("jar" + File.separator + name + "-" + version);
                    FileUtil.writeCSV(info, mavenPath);
                    continue;
                }
                long time2 = System.currentTimeMillis();
                logger.info(downloadName + " 分析耗时：" + (time2 - time1) + "ms");
                CSVOperator.save(ObjectGenerator.getMavenRepository(name, version, url, path));
                CSVOperator.saveAnalysisPackage(name, version, ap);
                deleteFile(path);
                deleteFolder("jar" + File.separator + name + "-" + version);
                line[3] = "1";
                long endTime = System.currentTimeMillis();
                sumTime += endTime - startTime;
                logger.info(downloadName + " 数据保存耗时：" + (endTime - time2) + "ms");
                logger.info(downloadName + " 分析完成，总耗时" + (endTime - startTime) + "ms");
                logger.info(String.format("已完成进度：%d/%d, 预计剩余时间：%ds", i + 1, info.size(), (info.size() - i) * sumTime / i / 1000));
                FileUtil.writeCSV(info, mavenPath);
            }
            logger.info("总分析数量：" + (info.size()-1));
            logger.info("总耗时：" + sumTime + "ms");
            logger.info("平均耗时：" + sumTime / (info.size()-1) + "ms");
        } catch (IOException e) {
            logger.info(e.getMessage());
        } finally {
            assert info != null;
            FileUtil.writeCSV(info, mavenPath);
        }
    }
}
