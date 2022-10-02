package pers.ailurus.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "relation_class_method")
public class RelationClassMethod {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "class_md5")
    private String classMd5;

    @TableField(value = "method_md5")
    private String methodMd5;

    public static final String COL_ID = "id";

    public static final String COL_CLASS_MD5 = "class_md5";

    public static final String COL_METHOD_MD5 = "method_md5";

    public RelationClassMethod(String md5, String md51) {
        this.classMd5 = md5;
        this.methodMd5 = md51;
    }
}