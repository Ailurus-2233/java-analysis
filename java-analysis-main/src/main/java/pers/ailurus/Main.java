package pers.ailurus;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.devtools.common.options.OptionsParser;
import pers.ailurus.args.Options;
import pers.ailurus.model.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        // 分析参数
//        args = new String[]{
//                "--analysis",
//                "--batch",
//                "--csv_file", "C:\\Users\\ailur\\Documents\\iie_work\\maven\\target\\maven_50k_dataset.csv",
//                "--output_path", "C:\\Users\\ailur\\Documents\\iie_work\\maven\\result"
//        };
        // 解析参数
//        args = new String[]{
//                "--batch",
//                "--csv_file", "C:\\Users\\ailur\\Documents\\iie_work\\maven\\target\\speculation_dataset.csv",
//        };
        args = new String[] {
                "--jar_file", "D:\\Research\\Java TPL Predict\\difference_extract\\demo\\group_2\\cybrid-api-organization-java-v0.10.0.jar",
                "--output_path", "D:\\Research\\Java TPL Predict\\difference_extract\\demo\\group_2"
        };
        OptionsParser parser = OptionsParser.newOptionsParser(Options.class);
        parser.parseAndExitUponError(args);
        Options options = parser.getOptions(Options.class);

        if (options.isAnalysis)
            if (options.isBatch) {
                try {
                    analysisBatch(options.csvFile, options.outputPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if ("".equals(options.jarFile))
                analysisSingleNet(options.netFile, options.outputPath);
            else
                analysisSingle(options.jarFile, options.outputPath);
        else if (options.isBatch)
            matchBatch(options.csvFile, options.outputPath);
        else if ("".equals(options.jarFile))
            matchSingleNet(options.netFile, options.outputPath);
        else {
            try {
                matchSingle(options.jarFile, options.outputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 结束程序
        System.exit(0);
    }

    public static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar Main.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),
                OptionsParser.HelpVerbosity.LONG));
    }

    public static void analysisBatch(String csvFile, String outputPath) throws IOException {
        CsvReader reader = CsvUtil.getReader();
        List<AnalysisBean> info = reader.read(
                ResourceUtil.getUtf8Reader(csvFile), AnalysisBean.class);

        //初始化CSV文件
        CSVOperator.initCSVFile(outputPath);

        int count = 0;

        for (AnalysisBean ab : info) {
            long start = System.currentTimeMillis();
            AnalysisPackage ap = null;

            try {
                ap = Extractor.extract(ab.getFilePath(), 10);
            } catch (TimeoutException e) {
                Console.log("{} 解析超时", ab.getFilePath());
                continue;
            }

            if (ap.getClassNum() == 0) {
                Console.log("{} 没有class文件", ab.getFilePath());
                continue;
            }

            CSVOperator.saveAnalysisPackage(ab.getGroupId(), ab.getArtifactId(), ab.getVersion(), ap);
            CSVOperator.save(new MavenRepository(ab.getGroupId(), ab.getArtifactId(), ab.getVersion(), ab.getUrl(), Long.parseLong(ab.getFileSize())));
            long end = System.currentTimeMillis();
            Console.log("{} 解析完毕，用时 {} ms", ab.getFilePath(), end - start);
        }
    }

    public static void analysisSingle(String jarFile, String outputPath) {

    }

    public static void matchBatch(String csvFile, String outputPath) {
        CsvReader reader = CsvUtil.getReader();
        List<MatchBean> info = reader.read(
                ResourceUtil.getUtf8Reader(csvFile), MatchBean.class);
        for (MatchBean mb : info) {
            Console.print(mb);
            String path = mb.getPath();
            AnalysisPackage ap = Extractor.extract(path);
            File temp = new File(path);
            String fileName = temp.getName();
            String file = outputPath + File.separator + fileName + ".json";
            MyFileUtil.dacFile(file);
            MyFileUtil.writeLine(JSONUtil.parseObj(ap).toStringPretty(), file);
        }
    }

    public static void matchSingle(String jarFile, String outputPath) throws IOException {
        AnalysisPackage ap = Extractor.extract(jarFile);
        JSONObject ans = JSONUtil.parseObj(ap);
        File temp = new File(jarFile);
        String file = outputPath + File.separator + temp.getName() +"_result.json";
        MyFileUtil.dacFile(file);
        MyFileUtil.writeLine(ans.toStringPretty(), file);
    }

    private static void matchSingleNet(String netFile, String outputPath) {

    }

    private static void analysisSingleNet(String netFile, String outputPath) {

    }
}
