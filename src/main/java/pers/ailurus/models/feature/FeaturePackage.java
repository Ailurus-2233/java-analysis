package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import lombok.Data;

import java.util.List;

@Data
public class FeaturePackage {

    private String md5; // 根据class的MD5集合生成的标识
    private int classNum;
    private int packageDeep;
    private int packageNum;
    private int fieldNum;
    private int methodNum;
    private List<FeatureClass> classes;
    private CDG cdg;

    public Dict toDict() {
        Dict classes = Dict.create();
        this.sortClasses();
        for (FeatureClass clazz : this.classes) {
            classes.set(clazz.getMd5(), clazz.toDict());
        }
        return Dict.create()
                .set("md5", md5)
                .set("class_num", classNum)
                .set("package_deep", packageDeep)
                .set("package_num", packageNum)
                .set("field_num", fieldNum)
                .set("method_num", methodNum)
                .set("classes", classes);
    }

    public void sortClasses() {
        this.classes.sort((o1, o2) -> {
            if (o1.getClassId() > o2.getClassId()) {
                return 1;
            } else if (o1.getClassId() < o2.getClassId()) {
                return -1;
            } else {
                return 0;
            }
        });
    }
}
