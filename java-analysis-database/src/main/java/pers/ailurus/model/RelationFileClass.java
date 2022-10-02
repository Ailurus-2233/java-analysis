package pers.ailurus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationFileClass {
    private String fileMd5;

    private String classMd5;

    public String toCSVLine() {
        return String.format("%s,%s", fileMd5, classMd5);
    }
}