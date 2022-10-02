package pers.ailurus.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "relation_file_class")
public class RelationFileClass {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "file_md5")
    private String fileMd5;

    @TableField(value = "class_md5")
    private String classMd5;

    public static final String COL_ID = "id";

    public static final String COL_FILE_MD5 = "file_md5";

    public static final String COL_CLASS_MD5 = "class_md5";

    public RelationFileClass(String md5, String md51) {
        this.fileMd5 = md5;
        this.classMd5 = md51;
    }
}