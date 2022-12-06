package pers.ailurus.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureClass implements Serializable {
    private String md5;

    private Integer modifier;

    private Integer interfaceNum;

    private Integer hasSuperClass;

    private Integer fieldNum;

    private Integer methodNum;

    private Integer depClassNum;

    private Integer beDepNum;

    public String toCSVLine() {
        return String.format("%s,%d,%d,%d,%d,%d,%d,%d", md5, modifier, interfaceNum, hasSuperClass, fieldNum, methodNum, depClassNum, beDepNum);
    }
}