import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.lang.Console;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsParser;
import pers.ailurus.models.feature.FeaturePackage;
import pers.ailurus.utils.Analysis;
import pers.ailurus.utils.Args;
import pers.ailurus.utils.Config;
import pers.ailurus.utils.FileOperator;

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final Log log = LogFactory.get();

    private static int downloadMaxSize = 5 * 1024 * 1024;
    private static Map<Integer, Integer> option = MapUtil.builder(new HashMap<Integer, Integer>())
            .put(20, 100)
            .put(100, 50)
            .put(500, 20)
            .put(1000, 10)
            .put(5000, 5)
            .build();

    private static int downloadFlag = 0;

    public static void main(String[] args) {
        OptionsParser parser = OptionsParser.newOptionsParser(Args.class);
        parser.parseAndExitUponError(args);
        Args options = parser.getOptions(Args.class);
        if (options.input.equals("")) {
            Console.print("Input path is empty\n");
            printUsage(parser);
            return;
        }
        switch (options.mode) {
            case "analysis":
                analysis(options);
                break;
            case "predict":
                break;
            case "download":
                download(options);
                break;
            default:
                Console.print("Unknown mode: {}\n", options.mode);
                printUsage(parser);
        }
    }

    public static void printUsage(OptionsParser parser) {
        Console.print("Usage: java -jar analysis.jar OPTIONS\n");
        Console.print("{}\n", parser.describeOptions(Collections.emptyMap(), OptionsParser.HelpVerbosity.LONG));
    }

    public static CsvData loadCSV(String path) {
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file(path));
        return data;
    }

    public static void download(Args option) {
        if (option.isBatch) {
            // 如果不是csv文件，直接返回
            if (!option.input.endsWith(".csv")) {
                log.error("The input file is not a csv file!");
                return;
            }
            CsvData csv = loadCSV(option.input);
            List<CsvRow> rows = csv.getRows();
            // CSV 的列包括 url,group_id,article_id,version,status
            // 多线程下载
            ExecutorService executor = Executors.newFixedThreadPool(option.thread);
            for (int i = 1; i < rows.size(); i++) {
                String status = rows.get(i).get(4);
                if (!("0".equals(status) || "-1".equals(status))) {
                    continue;
                }
                int t = i;
                executor.execute(() -> {
                    downloadFlag++;
                    CsvRow row = rows.get(t);
                    String url = row.get(0);
                    String group_id = row.get(1);
                    String article_id = row.get(2);
                    String version = row.get(3);
                    String outputPath = FileUtil.file(option.output, group_id, article_id, String.format("%s-%s.jar", article_id, version)).getAbsolutePath();
                    row.set(4, String.valueOf(download(url, outputPath)));
                    // 保存csv
                    if (t % 500 == 0) {
                        CsvUtil.getWriter(option.input, CharsetUtil.CHARSET_UTF_8).write(csv);
                    }
                    downloadFlag--;
                });
            }
            while (downloadFlag > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CsvUtil.getWriter(option.input, CharsetUtil.CHARSET_UTF_8).write(csv);
            executor.shutdown();
        } else {
            download(option.input, option.output);
        }
        log.info("The download task is complete!");
    }

    public static int download(String input, String output) {
        try {
            log.info("Url:{}", input);
            long fileSize = FileOperator.getNetFileSize(input);
            while (fileSize == 0) {
                Thread.sleep(1000);
                fileSize = FileOperator.getNetFileSize(input);
            }
            // 如果文件已经存在，且大小一致，则跳过
            File target = getTarget(input, output, "jar");
            if (target.exists() && FileUtil.size(target) == fileSize) {
                log.info("The file already exists, skip download!");
                return 1;
            }

            // 检查文件大小
            if (checkSize(fileSize) != 0) return checkSize(fileSize);

            // 下载文件
            log.info("Start downloading...");
            output = FileOperator.downloadFile(input, FileUtil.file(output));
        } catch (Exception e) {
            log.error("The url is invalid or the network is not available!!!");
            return -1;
        }
        // 下载完成
        log.info("Download finished!");
        log.info("Output: {}", output);
        return 1;
    }

    public static int checkSize(long fileSize) {
        log.info("File size: {}", fileSize);
        // 如果文件过大，跳过
        if (fileSize > downloadMaxSize) {
            log.error("The file is too large, skip download!");
            return -2;
        }
        // 如果文件过小，跳过
        if (fileSize < 1024 && fileSize > 0) {
            log.error("The file is too small, skip download!");
            return -3;
        }
        return 0;
    }

    public static File getTarget(String input, String output, String fileType) {
        File target = new File(output);
        if (!FileOperator.isFile(target)) {
            FileUtil.mkdir(output);
            String fileName = input.substring(input.lastIndexOf("/") + 1);
            fileName = fileName.substring(0, fileName.lastIndexOf(".") + 1);
            target = FileUtil.file(output, fileName + fileType);
        }
        return target;
    }

    public static void analysis(Args option) {
        if (option.isBatch) {

        } else {
            analysis(option.input, option.output, option.config, option.groupId, option.artifactId, option.version);
        }
    }

    public static void analysis(String input, String output, String configPath, String group, String artifact, String version) {
        // 判断输入文件是否是jar文件
        if (!input.endsWith(".jar")) {
            log.error("The input file is not a jar file!");
            return;
        }

        // 读取配置文件
        FileReader fileReader = new FileReader(configPath);
        Config config = JSONUtil.toBean(fileReader.readString(), Config.class);

        // 分析jar文件
        FeaturePackage fp = Analysis.analysisPackage(input, group, artifact, version);
        if (config.isSave) {
            // TODO 保存到数据库
        }

        // 保存到文件
        FileOperator.writeFile(JSONUtil.toJsonStr(fp.toSave()), getTarget(input, output, "json").getAbsolutePath());
    }
}