package pers.ailurus.args;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;


public class Options extends OptionsBase {
    @Option(name = "analysis", help = "是否是分析模式, 分析模式用于提取已知TPL的特征, 匹配模式是对未知TPL进行匹配", defaultValue = "false")
    public boolean isAnalysis;
    @Option(name = "batch", help = "是否为批量模式, 批量模式为读取CSV文件, 一次解析多个TPL, 单一模式直接读取Jar文件, 可以是jar文件路径，也可以是url", defaultValue = "false")
    public boolean isBatch;
    @Option(name = "jar_file", help = "待分析的Jar文件路径", defaultValue = "")
    public String jarFile;
    @Option(name = "net_file", help = "使用url下载待分析Jar文件", defaultValue = "")
    public String netFile;
    @Option(name = "csv_file", help = "batch 模式下记录待解析文件的csv文件路径，其中的字段有:" +
            "\n\t分析模式:group_id,artifact_id,version,file_name,url,flag,file_size,file_path" +
            "\n\t匹配模式:file_name,flag,file_path", defaultValue = "")
    public String csvFile;
    @Option(name = "output_path", help = "结果输出路径", defaultValue = "./result")
    public String outputPath;
}
