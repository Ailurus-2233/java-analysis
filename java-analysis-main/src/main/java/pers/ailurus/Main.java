package pers.ailurus;

import com.google.devtools.common.options.OptionsParser;
import pers.ailurus.args.Options;

import java.util.Collections;

public class Main {

    public static void main(String[] args) {
        args = new String[]{"--jar_file", "a", "--output_file", "b"};
//        args = new String[]{"--help"};
        OptionsParser parser = OptionsParser.newOptionsParser(Options.class);
        parser.parseAndExitUponError(args);
        Options options = parser.getOptions(Options.class);
        System.out.printf("%s\n%s\n%s", options.jarFile, options.outputFile, options.netFile);


        // 结束程序
        System.exit(0);
    }

    public static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar Main.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),
                OptionsParser.HelpVerbosity.LONG));
    }
}
