package pers.ailurus.model;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class AnalysisBean {
    @Alias("group_id")
    private String groupId;
    @Alias("artifact_id")
    private String artifactId;
    private String version;
    @Alias("file_name")
    private String fileName;
    private String url;
    private String flag;
    @Alias("file_size")
    private String fileSize;
    @Alias("file_path")
    private String filePath;
}
