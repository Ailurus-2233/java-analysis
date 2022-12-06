package pers.ailurus.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureFile implements Serializable {
    private String md5;

    private String name;

    private String version;

    private Integer classNum;

    private Integer packageDeep;

    private Integer packageNum;

    public String toCSVLine() {
        return String.format("%s,%s,%s,%d,%d,%d", md5, name, version, classNum, packageDeep, packageNum);
    }
}