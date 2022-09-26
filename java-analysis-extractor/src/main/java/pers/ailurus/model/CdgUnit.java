package pers.ailurus.model;

import lombok.Data;

import java.util.List;

@Data
public class CdgUnit {
    private int id;
    private List<Integer> dependencies;
    private String className;

    public CdgUnit(String className, int i) {
        this.className = className;
        this.id = i;
    }

    public String toString() {
        return String.format("%s: (%d, %s)", this.className, this.id, this.dependencies.toString());
    }
}
