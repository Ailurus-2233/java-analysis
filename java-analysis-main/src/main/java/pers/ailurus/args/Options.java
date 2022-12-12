package pers.ailurus.args;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;


public class Options extends OptionsBase {
    @Option(name = "csv_file", help = "待下载TPL的CSV文件", defaultValue = "")
    public String csvFile;
    @Option(name = "output_folder", help = "结果输出路径", defaultValue = "")
    public String outputFolder;
}
