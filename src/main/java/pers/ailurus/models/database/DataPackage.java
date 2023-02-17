package pers.ailurus.models.database;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.ailurus.models.feature.FeaturePackage;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPackage {
    // 唯一标识
    @Alias("md5")
    private String md5;

    // 版本信息
    @Alias("group_id")
    private String groupId;
    @Alias("artifact_id")
    private String artifactId;
    private String version;

    // 特征信息
    @Alias("class_num")
    private int classNum;
    @Alias("package_deep")
    private int packageDeep;
    @Alias("package_num")
    private int packageNum;
    @Alias("field_num")
    private int fieldNum;
    @Alias("method_num")
    private int methodNum;

    public DataPackage(FeaturePackage fp, String groupId, String artifactId, String version) {
        this.md5 = fp.getMd5();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classNum = fp.getClassNum();
        this.packageDeep = fp.getPackageDeep();
        this.packageNum = fp.getPackageNum();
        this.fieldNum = fp.getFieldNum();
        this.methodNum = fp.getMethodNum();
    }

    public String[] toCSV() {
        return new String[] {
            this.md5,
            this.groupId,
            this.artifactId,
            this.version,
            String.valueOf(this.classNum),
            String.valueOf(this.packageDeep),
            String.valueOf(this.packageNum),
            String.valueOf(this.fieldNum),
            String.valueOf(this.methodNum)
        };
    }
}
