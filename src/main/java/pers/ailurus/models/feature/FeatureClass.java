package pers.ailurus.models.feature;

import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.ailurus.utils.Analysis;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureClass {

    // 根据方法计算的唯一标识
    private String md5;

    // 基本信息
    private int id;
    private String name;

    // 特征
    private int[] base;     // [接口数量，注解数量，属性数量，方法数量]
    private int[] field;    // [mapping(x) for x in filed]

    // 方法列表
    private FeatureMethod[] methods;

    public FeatureClass(int id, String name) {
        this.id = id;
        this.name = name;

        this.base = new int[4];
    }

    public void analysisClass(SootClass clazz, HashMap<String, Integer> classMap) {
        // 基础特征
        this.base[0] = clazz.getInterfaceCount();
        this.base[2] = clazz.getFields().size();
        this.base[3] = clazz.getMethods().size();
        List<Tag> tags = clazz.getTags();
        int annotationNum = 0;
        for (Tag tag : tags) {
            if (tag instanceof VisibilityAnnotationTag vat) {
                annotationNum = vat.getAnnotations().size();
                break;
            }
        }
        this.base[1] = annotationNum;

        // 属性特征
        this.field = new int[this.base[2]];
        List<SootField> fields = new ArrayList<>(clazz.getFields());
        for (int i = 0; i < fields.size(); i++) {
            SootField field = fields.get(i);
            String type = field.getType().toString();
            this.field[i] = Analysis.getTypeId(type, classMap);
        }
        Arrays.sort(this.field);

        // 方法特征
        List<FeatureMethod> methods = new ArrayList<>();
        List<SootMethod> sms = new ArrayList<>(clazz.getMethods());
        for (SootMethod sm : sms) {
            FeatureMethod method = new FeatureMethod();
            if (method.analysisMethod(sm, classMap))
                methods.add(method);
            else
                this.base[3]--;
        }
        methods.sort((o1, o2) -> {
            int[] base1 = o1.getBase();
            int[] base2 = o2.getBase();
            int ans = base1[0] - base2[0] == 0 ? base1[1] - base2[1] : base1[0] - base2[0];
            if (ans != 0) {
                return ans;
            }
            int len1 = o1.getParam().length;
            int len2 = o2.getParam().length;
            if (len1 != len2) {
                return len1 - len2;
            }
            len1 = o1.getInvoke().length;
            len2 = o2.getInvoke().length;
            return len1 - len2;
        });
        this.methods = methods.toArray(new FeatureMethod[0]);

        // 计算md5
        for (FeatureMethod method : this.methods) {
            this.md5 += method.getMd5();
        }
        this.md5 = DigestUtil.md5Hex(this.md5);
    }

    public Dict toDict() {
        return Dict.create()
                .set("id", this.id)
                .set("name", this.name)
                .set("base", this.base)
                .set("field", this.field)
                .set("methods", Arrays.stream(this.methods).map(FeatureMethod::toDict).toArray());
    }
}
