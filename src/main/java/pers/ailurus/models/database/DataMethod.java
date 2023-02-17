package pers.ailurus.models.database;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.ailurus.models.feature.FeatureMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataMethod {
    // 唯一标识
    private String md5;

    // 特征信息
    private String signature;
    private int modifier;
    @Alias("args_num")
    private int argsNum;
    @Alias("args_type")
    private String argsType;
    @Alias("return_type")
    private int returnType;
    @Alias("cfg_finger")
    private String cfgFinger;
    @Alias("local_num")
    private int localNum;
    @Alias("local_type")
    private String localType;
    @Alias("constant_num")
    private int constantNum;
    private String constants;

    public DataMethod(FeatureMethod featureMethod) {
        this.md5 = featureMethod.getMd5();
        this.signature = ArrayUtil.toString(featureMethod.getSignature());
        this.modifier = featureMethod.getModifier();
        this.argsNum = featureMethod.getArgsNum();
        this.argsType = ArrayUtil.toString(featureMethod.getArgsType());
        this.returnType = featureMethod.getReturnType();
        this.cfgFinger = featureMethod.getCfgFinger();
        this.localNum = featureMethod.getLocalNum();
        this.localType = ArrayUtil.toString(featureMethod.getLocalType());
        this.constantNum = featureMethod.getConstantNum();
        StringBuilder sb = new StringBuilder();
        for (String s: featureMethod.getConstants()) {
            sb.append(s);
        }
        this.constants = sb.toString();
    }

    public String[] toCSV() {
        return new String[]{
                this.md5,
                this.signature,
                String.valueOf(this.modifier),
                String.valueOf(this.argsNum),
                this.argsType,
                String.valueOf(this.returnType),
                this.cfgFinger,
                String.valueOf(this.localNum),
                this.localType,
                String.valueOf(this.constantNum),
                this.constants
        };
    }
}
