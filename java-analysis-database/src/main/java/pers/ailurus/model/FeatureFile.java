package pers.ailurus.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "feature_file")
public class FeatureFile {
    @TableId(value = "md5", type = IdType.INPUT)
    private String md5;

    @TableField(value = "`name`")
    private String name;

    @TableField(value = "version")
    private String version;

    @TableField(value = "class_num")
    private Integer classNum;

    @TableField(value = "package_deep")
    private Integer packageDeep;

    @TableField(value = "package_num")
    private Integer packageNum;

    public static final String COL_MD5 = "md5";

    public static final String COL_NAME = "name";

    public static final String COL_VERSION = "version";

    public static final String COL_CLASS_NUM = "class_num";

    public static final String COL_PACKAGE_DEEP = "package_deep";

    public static final String COL_PACKAGE_NUM = "package_num";
}