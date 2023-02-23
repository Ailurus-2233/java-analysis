package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import lombok.Data;

import java.util.*;

@Data
public class CDGNode {
    private int id;
    private int type;
    private HashSet<CDGNode> dependencies;
    private HashSet<CDGNode> beDependencies;
    private String className;
    private int weight;

    public CDGNode(int i, String className, int type) {
        this.className = className;
        this.id = i;
        this.type = type;
        this.dependencies = new HashSet<>();
        this.beDependencies = new HashSet<>();
    }

    public void addDependent(CDGNode node) {
        this.dependencies.add(node);
    }

    public void addBeDependent(CDGNode node) {
        this.beDependencies.add(node);
    }

    public Dict toDict() {
        int[] dep = new int[this.dependencies.size()];
        for (int i = 0; i < dep.length; i++) {
            dep[i] = this.dependencies.toArray(new CDGNode[0])[i].getId();
        }
        Arrays.sort(dep);
        return Dict.create()
                .set("id", this.id)
                .set("type", this.type)
                .set("className", this.className)
                .set("weight", this.weight)
                .set("dependencies", dep);
    }

    @Override
    public int hashCode() {
        return this.id + this.className.hashCode() + this.type;
    }

    @Override
    public String toString() {
        return String.format(
                "id: %d, type: %d, name: %s",
                this.id,
                this.type,
                this.className
        );
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CDGNode) {
            CDGNode node = (CDGNode) obj;
            return this.id == node.getId() && this.className.equals(node.getClassName()) && this.type == node.getType();
        }
        return false;
    }

    public String getHashString() {
        int[] dep = new int[this.dependencies.size()];
        for (int i = 0; i < dep.length; i++) {
            dep[i] = this.dependencies.toArray(new CDGNode[0])[i].getId();
        }
        Arrays.sort(dep);
        return String.format(
                "%d%d%s",
                this.id,
                this.weight,
                Arrays.toString(dep)
        );
    }

    public void removeDependent(CDGNode node) {
        if (!this.dependencies.remove(node)) {
            Iterator<CDGNode> iterator = this.dependencies.iterator();
            while (iterator.hasNext()) {
                CDGNode next = iterator.next();
                if (next.equals(node)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public void removeBeDependent(CDGNode node) {
        if (!this.beDependencies.remove(node)) {
            Iterator<CDGNode> iterator = this.beDependencies.iterator();
            while (iterator.hasNext()) {
                CDGNode next = iterator.next();
                if (next.equals(node)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
