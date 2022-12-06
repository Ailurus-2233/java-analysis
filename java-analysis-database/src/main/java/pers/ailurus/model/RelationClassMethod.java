package pers.ailurus.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationClassMethod implements Serializable {
    private String classMd5;

    private String methodMd5;

    public String toCSVLine() {
        return String.format("%s,%s", classMd5, methodMd5);
    }
}