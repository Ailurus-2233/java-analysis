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
import static pers.ailurus.NetUtil.downloadWithCheckSize;

public class MavenMain {
    private static Logger logger = LoggerFactory.getLogger(MavenMain.class);
    static List<String[]> info;
    static String mavenPath;

    public static void main(String[] args) {
        mavenPath = args[0];
        try {
            long sumTime = 0;
            int analysisCount = 0;

            // 读取maven仓库信息
            info = readCSVFile(mavenPath);

            // 初始化数据存储文件
            CSVOperator.initCSVFile(getFileNameWithOutSuffix(mavenPath));

            // 逐个分析
            for (int i = 1; i < info.size(); i++) {
                long startTime = System.currentTimeMillis();

                // 1. 获取 TPL 基本信息 名称，版本，下载地址，当前状态，保存路径
                String[] line = info.get(i);
                String name = line[0];
                String version = line[1];
                String url = line[2];
                String flag = line[3];
                String downloadName = String.format("%s-%s.jar", name, version);
                String path = String.format("jar%s%s", File.separator, downloadName);

                // 判断是否分析过，或者存在其他问题
                if ("1".equals(flag) || "-1".equals(flag) || "-2".equals(flag) || "-3".equals(flag)) {
                    logger.info(String.format("[%s] Analyzed.", downloadName));
                    continue;
                }

                // 2. 下载第TPL到本地
                logger.info(String.format("[%s] Start analysis.", downloadName));
                // 使用国内源
                String urlCN = url.replace("https://repo1.maven.org/maven2/", "https://maven.aliyun.com/repository/public/");
                boolean downloadResult = downloadWithCheckSize(urlCN, downloadName, "jar", 1024 * 1024 * 5);
                if (!downloadResult) {
                    downloadResult = downloadWithCheckSize(url, downloadName, "jar", 1024 * 1024 * 5);
                }
                if (!downloadResult) {
                    stopAnalysis(path, "-3", i);
                    continue;
                }
                long time1 = System.currentTimeMillis();
                logger.info(String.format("[%s] Download finished, time: %d ms.", downloadName, time1 - startTime));

                // 3. 分析本地TPL
                AnalysisPackage ap = null;
                try {
                    ap = extract(path, 120);
                } catch (TimeoutException e) {
                    logger.error(String.format("[%s] Analysis timeout.", downloadName));
                    stopAnalysis(path, "-2", i);
                    continue;
                } catch (Exception e) {
                    logger.error(String.format("[%s] Execution error.", downloadName));
                    stopAnalysis(path, "-1", i);
                    continue;
                }
                long time2 = System.currentTimeMillis();
                logger.info(String.format("[%s] Analysis finished, time: %d ms.", downloadName, time2 - time1));

                // 4. 保存分析数据
                CSVOperator.save(ObjectGenerator.getMavenRepository(name, version, url, path));
                CSVOperator.saveAnalysisPackage(name, version, ap);

                // 5. 分析结束并保存状态
                stopAnalysis(path, "1", i);

                // 6. 其他信息统计
                analysisCount++;
                long endTime = System.currentTimeMillis();
                sumTime += endTime - startTime;
                logger.info(String.format("[%s] Data saved, time: %d ms.", downloadName, endTime - time2));
                logger.info(String.format("[%s] Total time: %d ms.", downloadName, endTime - startTime));
                logger.info(String.format("Progress: %d/%d, remain time: %d s.", i, info.size()-1, (info.size() - 1 - i) * (endTime - startTime) / 1000));
            }
            logger.info(String.format("Total number of TPL: %d.", analysisCount));
            logger.info(String.format("Total time: %d s.", sumTime / 1000));
            logger.info(String.format("Average time: %d ms.", sumTime / (analysisCount == 0 ? 1 : analysisCount)));
            resultCount(mavenPath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            assert info != null;
            FileUtil.writeCSV(info, mavenPath);
            System.exit(0);
        }
    }

    public static void stopAnalysis(String tplPath, String flag, int index) {
        info.get(index)[3] = flag;
        deleteFile(tplPath);
        deleteFolder(tplPath.replace(".jar", ""));
        FileUtil.writeCSV(info, mavenPath);
    }

    public static void resultCount(String mavenPath) {
        int finished = 0;
        int timeout = 0;
        int error = 0;
        int notDownload = 0;
        for (int i = 1; i < info.size(); i++) {
            String flag = info.get(i)[3];
            if ("1".equals(flag)) {
                finished++;
            } else if ("-1".equals(flag)) {
                error++;
            } else if ("-2".equals(flag)) {
                timeout++;
            } else if ("-3".equals(flag)) {
                notDownload++;
            }
        }
        logger.info(String.format("Finished: %d, Error: %d, Timeout: %d, Not Download: %d", finished, error, timeout, notDownload));

        FileUtil.writeLine(String.format("%d,%d,%d,%d\n", finished, error, timeout, notDownload), mavenPath.replace(".csv", "_result.txt"));
    }

}
