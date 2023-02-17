import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.google.devtools.common.options.OptionsParser;
import pers.ailurus.utils.Args;
import pers.ailurus.utils.CsvOperator;
import pers.ailurus.utils.FileOperator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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
            case "analysis_1":
                break;
            case "analysis_2":
                break;
            case "predict":
                break;
            case "download":
                download(options.input, options.output, options.isBatch);
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

    public static void download(String input, String output, boolean batch) {
        if (batch) {
            // 如果不是csv文件，直接返回
            if (!input.endsWith(".csv")) {
                log.error("The input file is not a csv file!");
                return;
            }
            CsvData csv = loadCSV(input);
            List<CsvRow> rows = csv.getRows();
            // CSV 的列包括 url,group_id,article_id,version,status
            // 多线程下载
            ExecutorService executor = Executors.newFixedThreadPool(8);
            for (int i = 1; i < rows.size(); i++) {
                String status = rows.get(i).get(4);
                if (!("0".equals(status) || "-1".equals(status))) {
                    continue;
                }
                int t = i;
                executor.execute(() -> {
                    CsvRow row = rows.get(t);
                    String url = row.get(0);
                    String group_id = row.get(1);
                    String article_id = row.get(2);
                    String version = row.get(3);
                    String outputPath = FileUtil.file(output, group_id, article_id, String.format("%s-%s.jar", article_id, version)).getAbsolutePath();
                    row.set(4, String.valueOf(download(url, outputPath)));
                    // 保存csv
                    if (t % 500 == 0) {
                        CsvUtil.getWriter(input, CharsetUtil.CHARSET_UTF_8).write(csv);
                    }
                });

            }
            CsvUtil.getWriter(input, CharsetUtil.CHARSET_UTF_8).write(csv);
        } else {
            download(input, output);
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
            File target = new File(output);
            if (!FileOperator.isFile(target)) {
                FileUtil.mkdir(output);
                target = FileUtil.file(output, input.substring(input.lastIndexOf("/") + 1));
            }
            if (target.exists() && FileUtil.size(target) == fileSize) {
                log.info("The file already exists, skip download!");
                return 1;
            }
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

    public static void analysis(String input, String output, boolean isBatch, String mode) {
        if (isBatch) {
            // 初始化数据存储文件
            CsvOperator.init(output);

            CsvData csv = loadCSV(input);
            // CSV 的列包括 group_id,article_id,version,file_path

        } else {
            download(input, output);
        }
        log.info("The analysis is complete!");
    }

}