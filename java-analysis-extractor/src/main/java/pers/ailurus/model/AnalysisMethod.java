package pers.ailurus.model;

import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分析得到的方法的特征信息
 *
 * @author wzy
 * @date 2022/09/14
 */
@Data
public class AnalysisMethod {

    private String md5;
    private int modifier;
    private int argsNum;
    private int returnType;
    private String cfgFinger;
    private List<CfgUnit> cfg;

    private static Map<String, Integer> typeMap = new HashMap<>();
    static {
        typeMap.put("void", 0);
        typeMap.put("boolean", 1);
        typeMap.put("byte", 2);
        typeMap.put("short", 3);
        typeMap.put("int", 4);
        typeMap.put("long", 5);
        typeMap.put("float", 6);
        typeMap.put("double", 7);
        typeMap.put("char", 8);
        typeMap.put("java.lang.String", 9);
    }

    public void setReturnType(String type) {
        this.returnType = typeMap.getOrDefault(type, -1);
    }

    public void genMd5() {
        this.md5 = String.format("%s_%d_%d_%d", this.cfgFinger, this.modifier, this.returnType, this.argsNum);
        this.md5 = DigestUtils.md5Hex(this.md5);
    }
}
