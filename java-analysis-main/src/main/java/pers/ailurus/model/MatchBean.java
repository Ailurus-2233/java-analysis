package pers.ailurus.model;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class MatchBean {
    @Alias("group_id")
    private String groupId;
    @Alias("artifact_id")
    private String artifactId;
    private String version;
    private String path;
    @Alias("speculation_group_id")
    private String speculationGroupId;
    @Alias("speculation_artifact_id")
    private String speculationArtifactId;
    @Alias("speculation_version")
    private String speculationVersion;
}
