package pers.ailurus.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "feature_class")
public class FeatureClass {
    @TableId(value = "md5", type = IdType.INPUT)
    private String md5;

    @TableField(value = "modifier")
    private Integer modifier;

    @TableField(value = "interface_num")
    private Integer interfaceNum;

    @TableField(value = "has_super_class")
    private Integer hasSuperClass;

    @TableField(value = "field_num")
    private Integer fieldNum;

    @TableField(value = "method_num")
    private Integer methodNum;

    @TableField(value = "dep_class_num")
    private Integer depClassNum;

    @TableField(value = "be_dep_num")
    private Integer beDepNum;

    public static final String COL_MD5 = "md5";

    public static final String COL_MODIFIER = "modifier";

    public static final String COL_INTERFACE_NUM = "interface_num";

    public static final String COL_HAS_SUPER_CLASS = "has_super_class";

    public static final String COL_FIELD_NUM = "field_num";

    public static final String COL_METHOD_NUM = "method_num";

    public static final String COL_DEP_CLASS_NUM = "dep_class_num";

    public static final String COL_BE_DEP_NUM = "be_dep_num";
}