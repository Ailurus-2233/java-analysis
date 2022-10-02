package pers.ailurus.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "feature_method")
public class FeatureMethod {
    @TableId(value = "md5", type = IdType.INPUT)
    private String md5;

    @TableField(value = "modifier")
    private Integer modifier;

    @TableField(value = "args_num")
    private Integer argsNum;

    @TableField(value = "return_type")
    private Integer returnType;

    @TableField(value = "cfg_finger")
    private String cfgFinger;

    public static final String COL_MD5 = "md5";

    public static final String COL_MODIFIER = "modifier";

    public static final String COL_ARGS_NUM = "args_num";

    public static final String COL_RETURN_TYPE = "return_type";

    public static final String COL_CFG_FINGER = "cfg_finger";
}