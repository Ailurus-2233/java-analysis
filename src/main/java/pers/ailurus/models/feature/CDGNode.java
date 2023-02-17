package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
public class CDGNode {
    private int id;
    private int[] dependents;
    private int[] beDependents;
    private String className;

    private int structImportance;
    private int contentImportance;
    private int typeImportance;
    private int importance;

    public CDGNode(String className, int i) {
        this.className = className;
        this.id = i;
    }

    public String toString() {
        return String.format("%s: (%d, %s)", this.className, this.id, Arrays.toString(this.dependents));
    }

    public void changeIdByMap(Map<Integer, Integer> oldToNew) {
        this.id = oldToNew.get(this.id);
        for (int i = 0; i < this.dependents.length; i++) {
            this.dependents[i] = oldToNew.get(this.dependents[i]);
        }
        for (int i = 0; i < this.beDependents.length; i++) {
            this.beDependents[i] = oldToNew.get(this.beDependents[i]);
        }
    }

    public Dict toDict() {
        return Dict.create()
                .set("id", this.id)
                .set("className", this.className)
                .set("dependents", this.dependents)
                .set("beDependents", this.beDependents)
                .set("importance", this.importance)
                .set("structImportance", this.structImportance)
                .set("contentImportance", this.contentImportance)
                .set("typeImportance", this.typeImportance);
    }
}
