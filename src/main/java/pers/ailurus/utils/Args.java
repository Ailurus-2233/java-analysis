package pers.ailurus.utils;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class Args extends OptionsBase {

    @Option(
            name = "mode",
            abbrev = 'm',
            help = "Mode of the program: analysis , predict, download.",
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

    @Option(
            name = "config",
            abbrev = 'c',
            help = "Database Config file path.",
            defaultValue = "./config.json"
    )
    public String config;

    @Option(
            name = "group",
            abbrev = 'g',
            help = "Group id of the project.",
            defaultValue = ""
    )
    public String groupId;

    @Option(
            name = "artifact",
            abbrev = 'a',
            help = "Artifact id of the project.",
            defaultValue = ""
    )
    public String artifactId;

    @Option(
            name = "version",
            abbrev = 'v',
            help = "Version of the project.",
            defaultValue = ""
    )
    public String version;

    @Option(
            name = "thread",
            abbrev = 't',
            help = "Thread number of the download mode.",
            defaultValue = "10"
    )
    public int thread;
}
