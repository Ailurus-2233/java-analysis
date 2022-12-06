package pers.ailurus.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class CdgUnit implements Serializable {
    private int id;
    private int[] dependencies;
    private String className;

    public CdgUnit(String className, int i) {
        this.className = className;
        this.id = i;
    }

    public String toString() {
        return String.format("%s: (%d, %s)", this.className, this.id, Arrays.toString(this.dependencies));
    }

    public void setDependencies(List<Integer> dependencies) {
        this.dependencies = dependencies.stream().mapToInt(Integer::intValue).toArray();
    }

    public void setDependencies(int[] dependencies) {
        this.dependencies = dependencies;
    }
}
