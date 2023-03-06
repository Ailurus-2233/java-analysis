package pers.ailurus.models.feature;


import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soot.SootClass;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeaturePackage {

    private String md5; // 根据class的MD5集合生成的标识
    private long analysisTime; // 分析时间

    // 基本信息
    private String groupId;
    private String artifactId;
    private String version;

    // 特征信息
    private int[] base; // [类数量，包深度，包数量，属性数量，方法数量]

    // 类依赖图
    private CDG cdg;

    // 类的特征
    private FeatureClass[] classes;

    public FeaturePackage(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;

        this.base = new int[5];
    }

    public void analysisPackage(List<SootClass> classes, CDG cdg) {
        // 基础特征
        this.base[0] = classes.size(); // 类数量
        this.base[1] = 0;   // 包深度
        this.base[2] = 0;   // 包数量
        this.base[3] = 0;   // 属性数量
        this.base[4] = 0;   // 方法数量
        this.cdg = cdg;
        Set<String> packageSet = new HashSet<>();

        // 分析类
        this.classes = new FeatureClass[classes.size()];
        for (int i = 0; i < classes.size(); i++) {
            SootClass clazz = classes.get(i);
            // 包相关的特征处理
            packageSet.add(clazz.getPackageName());
            int deep = clazz.getPackageName().split("\\.").length;
            if (deep > this.base[1]) {
                this.base[1] = deep;
            }

            this.classes[i] = new FeatureClass(this.cdg.getClassMap().get(clazz.getName()), clazz.getName());
            this.classes[i].analysisClass(clazz, this.cdg.getClassMap());

            this.base[3] += this.classes[i].getBase()[2];
            this.base[4] += this.classes[i].getBase()[3];
        }
        this.base[2] = packageSet.size();
        Arrays.sort(this.classes, Comparator.comparingInt(FeatureClass::getId));

        // 计算MD5
        StringBuilder sb = new StringBuilder();
        for (FeatureClass featureClass : this.classes) {
            sb.append(featureClass.getMd5());
        }
        this.md5 = DigestUtil.md5Hex(sb.toString());
    }

    public Dict toDict() {
        return Dict.create()
                .set("md5", this.md5)
                .set("groupId", this.groupId)
                .set("artifactId", this.artifactId)
                .set("version", this.version)
                .set("base", this.base)
                .set("cdg_level1", this.cdg.getLevel1Finger())
                .set("cdg_level2", this.cdg.getLevel2Finger())
                .set("cdg_level3", this.cdg.getLevel3Finger())
                .set("classes", Arrays.stream(this.classes).map(FeatureClass::toDict).toArray());
    }

    public Dict toSave() {
        return Dict.create()
                .set("md5", this.md5)
                .set("groupId", this.groupId)
                .set("artifactId", this.artifactId)
                .set("version", this.version)
                .set("base", this.base)
                .set("cdg_level1", this.cdg.getLevel1Finger())
                .set("cdg_level2", this.cdg.getLevel2Finger())
                .set("cdg_level3", this.cdg.getLevel3Finger())
                .set("classes", Arrays.stream(this.classes).map(FeatureClass::toSave).toArray())
                .set("analysis_time", this.analysisTime);
    }
}
