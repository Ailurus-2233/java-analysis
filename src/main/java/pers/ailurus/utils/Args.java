package pers.ailurus.utils;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class Args extends OptionsBase {

    @Option(
            name = "mode",
            abbrev = 'm',
            help = "Mode of the program: analysis_1, analysis_2 , predict, download.",
            defaultValue = "predict"
    )
    public String mode;

    @Option(
            name = "batch",
            abbrev = 'b',
            help = "Whether to run in batch mode.",
            defaultValue = "false"
    )
    public boolean isBatch;

    @Option(
            name = "input",
            abbrev = 'i',
            help = "Input file. Jar file path or csv file path.",
            defaultValue = ""
    )
    public String input;

    @Option(
            name = "output",
            abbrev = 'o',
            help = "Output path of the result.",
            defaultValue = "./output"
    )
    public String output;



}
