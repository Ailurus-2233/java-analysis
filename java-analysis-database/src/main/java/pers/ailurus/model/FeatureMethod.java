package pers.ailurus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureMethod {
    private String md5;

    private Integer modifier;

    private Integer argsNum;

    private Integer returnType;

    private String cfgFinger;

    public String toCSVLine() {
        return String.format("%s,%d,%d,%d,%s", md5, modifier, argsNum, returnType, cfgFinger);
    }
}