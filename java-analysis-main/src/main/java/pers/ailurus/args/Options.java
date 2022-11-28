package pers.ailurus.args;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;


public class Options extends OptionsBase {
    @Option(name = "jar_file", help = "待分析的Jar文件路径", defaultValue = "")
    public String jarFile;
    @Option(name = "output_file", help = "结果输出路径", defaultValue = "")
    public String outputFile;
    @Option(name = "net_file", help = "使用url下载待分析Jar文件", defaultValue = "")
    public String netFile;
}
