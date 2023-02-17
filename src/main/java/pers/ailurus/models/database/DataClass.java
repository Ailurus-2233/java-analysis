package pers.ailurus.models.database;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.ailurus.models.feature.FeatureClass;
import pers.ailurus.models.feature.FeatureMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataClass {
    // 唯一标识
    private String md5;

    // 特征信息
    @Alias("class_id")
    private int classId;
    private int modifiers;
    @Alias("package_deep")
    private int packageDeep;
    @Alias("interface_num")
    private int interfaceNum;
    @Alias("annotation_num")
    private int annotationNum;
    @Alias("method_num")
    private int methodNum;
    @Alias("method_type")
    private String methodType;
    @Alias("field_num")
    private int fieldNum;
    @Alias("field_type")
    private String fieldType;
    @Alias("has_super_class")
    private int hasSuperClass;
    @Alias("dep_num")
    private int depNum;
    @Alias("be_dep_num")
    private int beDepNum;

    public DataClass(FeatureClass fc) {
        this.md5 = fc.getMd5();
        this.classId = fc.getClassId();
        this.modifiers = fc.getModifiers();
        this.packageDeep = fc.getPackageDeep();
        this.interfaceNum = fc.getInterfaceNum();
        this.annotationNum = fc.getAnnotationNum();
        this.methodNum = fc.getMethodNum();
        this.methodType = ArrayUtil.toString(fc.getMethodType());
        this.fieldNum = fc.getFieldNum();
        this.fieldType = ArrayUtil.toString(fc.getFieldType());
        this.hasSuperClass = fc.getHasSuperClass();
        this.depNum = fc.getDepNum();
        this.beDepNum = fc.getBeDepNum();
    }

    public String[] toCSV() {
        return new String[] {
            this.md5,
            String.valueOf(this.classId),
            String.valueOf(this.modifiers),
            String.valueOf(this.packageDeep),
            String.valueOf(this.interfaceNum),
            String.valueOf(this.annotationNum),
            String.valueOf(this.methodNum),
            this.methodType,
            String.valueOf(this.fieldNum),
            this.fieldType,
            String.valueOf(this.hasSuperClass),
            String.valueOf(this.depNum),
            String.valueOf(this.beDepNum)
        };
    }

}
