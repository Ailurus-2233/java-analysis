package pers.ailurus.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "maven_repository")
public class MavenRepository {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "`name`")
    private String name;

    @TableField(value = "version")
    private String version;

    @TableField(value = "url")
    private String url;

    @TableField(value = "`size`")
    private Long size;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_VERSION = "version";

    public static final String COL_URL = "url";

    public static final String COL_SIZE = "size";

    public MavenRepository() {

    }
    public MavenRepository(String name, String version, String url, long size) {
        this.name = name;
        this.version = version;
        this.url = url;
        this.size = size;
    }
}