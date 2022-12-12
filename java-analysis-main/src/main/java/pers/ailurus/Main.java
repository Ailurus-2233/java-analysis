package pers.ailurus;

import cn.hutool.core.text.csv.CsvRow;
import com.google.devtools.common.options.OptionsParser;
import pers.ailurus.args.Options;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        args = new String[]{"--csv_file", "C:\\Users\\ailur\\Desktop\\log4j.csv", "--output_folder", "C:\\Users\\ailur\\Desktop\\temp"};
//        args = new String[]{"--help"};
        OptionsParser parser = OptionsParser.newOptionsParser(Options.class);
        parser.parseAndExitUponError(args);
        Options options = parser.getOptions(Options.class);

        try {
            List<CsvRow> rows = MyFileUtil.readCSVFile(options.csvFile);
            for (CsvRow row : rows) {
                if (row.getOriginalLineNumber() == 0) continue;
                String fileName = String.format("%s-%s.jar", row.getRawList().get(0), row.getRawList().get(1));
                String url = row.getRawList().get(2);
                NetUtil.download(url, options.outputFolder + File.separator + fileName);
//                System.out.println(fileName);
//                System.out.println(url);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 结束程序
        System.exit(0);
    }

    public static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar Main.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),
                OptionsParser.HelpVerbosity.LONG));
    }
}
