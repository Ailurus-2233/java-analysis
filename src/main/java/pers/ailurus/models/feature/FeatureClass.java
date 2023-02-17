package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;

import java.util.List;

@Data
public class FeatureClass {
    private String md5;

    private int classId;
    private int modifiers;
    private int packageDeep;
    private int interfaceNum;
    private int annotationNum;
    private int methodNum;
    private int[] methodType;
    private int fieldNum;
    private int[] fieldType;
    private int hasSuperClass;
    private int depNum;
    private int beDepNum;
    private List<FeatureMethod> methods;

    private String classType;

    public void setMd5(String md5) {
        this.md5 = DigestUtil.md5Hex(String.format("%s_%d_%d_%d_%d_%d_%d_%d_%d",
                md5,
                classId,
                modifiers,
                packageDeep,
                interfaceNum,
                annotationNum,
                hasSuperClass,
                depNum,
                beDepNum
        ));
    }

    public Dict toDict() {
        Dict result =  Dict.create()
                .set("class_id", classId)
                .set("class_type", classType)
                .set("modifiers", modifiers)
                .set("package_deep", packageDeep)
                .set("interface_num", interfaceNum)
                .set("annotation_num", annotationNum)
                .set("method_num", methodNum)
                .set("method_type", methodType)
                .set("field_num", fieldNum)
                .set("field_type", fieldType)
                .set("has_super_class", hasSuperClass)
                .set("dep_num", depNum)
                .set("be_dep_num", beDepNum);
        Dict method = Dict.create();
        for (FeatureMethod fm : this.methods) {
            method.set(fm.getMd5(), fm.toDict());
        }
        result.set("methods", method);
        return result;
    }
}