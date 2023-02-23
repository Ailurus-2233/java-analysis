package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;
import soot.*;
import soot.util.Chain;

import java.util.*;

@Data
public class CDG {

    private static final Map<String, String> baseToClass = new HashMap<>();

    static {
        baseToClass.put("void", "java.lang.Void");
        baseToClass.put("boolean", "java.lang.Boolean");
        baseToClass.put("byte", "java.lang.Byte");
        baseToClass.put("short", "java.lang.Short");
        baseToClass.put("int", "java.lang.Integer");
        baseToClass.put("long", "java.lang.Long");
        baseToClass.put("float", "java.lang.Float");
        baseToClass.put("double", "java.lang.Double");
        baseToClass.put("char", "java.lang.Character");
    }


    private HashMap<String, Integer> classMap;
    private List<CDGNode> allNode;
    private int startNum;

    private List<CDGNode> level1;
    private List<CDGNode> level2;
    private List<CDGNode> level3;

    private String level1Finger;
    private String level2Finger;
    private String level3Finger;

    public CDG(List<SootClass> appClasses, List<SootClass> libClasses, List<SootClass> phantomClasses) {
        this.level1 = new ArrayList<>();
        this.level2 = new ArrayList<>();
        this.level3 = new ArrayList<>();
        // 根据类名生成类名到id的映射 依赖库,应用类,虚幻类的顺序
        this.classMap = new HashMap<>();
        this.allNode = new ArrayList<>();
        int i = 0;

        libClasses.sort(Comparator.comparing(SootClass::getName));
        for (SootClass sc : libClasses) {
            this.classMap.put(sc.getName(), i);
            this.allNode.add(new CDGNode(i++, sc.getName(), 2));
        }

        phantomClasses.sort(Comparator.comparing(SootClass::getName));
        for (SootClass sc : phantomClasses) {
            this.classMap.put(sc.getName(), i);
            this.allNode.add(new CDGNode(i++, sc.getName(), 3));
        }

        this.startNum = i;

        appClasses.sort(Comparator.comparing(SootClass::getName));
        for (SootClass sc : appClasses) {
            this.classMap.put(sc.getName(), i);
            this.allNode.add(new CDGNode(i, sc.getName(), 1));
            this.level1.add(new CDGNode(i, sc.getName(), 1));
            this.level2.add(new CDGNode(i, sc.getName(), 1));
            this.level3.add(new CDGNode(i++, sc.getName(), 1));
        }

        for (String key : baseToClass.keySet()) {
            this.classMap.put(key, this.classMap.get(baseToClass.get(key)));
        }

        initLevel1(appClasses);
        initLevel2(appClasses);
        initLevel3(appClasses);
    }

    private void addDependent(CDGNode src, String dstName, int level) {
        if (dstName.equals(src.getClassName())) {
            return;
        }
        if (baseToClass.containsKey(dstName)) {
            dstName = baseToClass.get(dstName);
        }
        if (!this.classMap.containsKey(dstName)) {
            return;
        }
        int index = this.classMap.get(dstName);
        CDGNode dst = index > this.allNode.size() ? this.allNode.get(index - this.allNode.size()) : this.allNode.get(index);
        if (dst.getType() != 1) {
            src.addDependent(dst);
        }
        if (dst.getType() == 1) {
            switch (level) {
                case 1 -> dst = this.level1.get(dst.getId() - this.startNum);
                case 2 -> dst = this.level2.get(dst.getId() - this.startNum);
                case 3 -> dst = this.level3.get(dst.getId() - this.startNum);
            }
            src.addDependent(dst);
            dst.addBeDependent(src);
        }
    }

    private void initLevel1(List<SootClass> appClasses) {
        // 根据父类依赖生成依赖关系
        for (int i = 0; i < appClasses.size(); i++) {
            SootClass sc = appClasses.get(i);
            if (sc.getName().contains("module-info")) {
                continue;
            }
            if (sc.hasSuperclass()) {
                String superClassName = sc.getSuperclass().getName();
                addDependent(this.level1.get(i), superClassName, 1);
            }
        }

        // 排序
        this.level1.sort((this::compareCDGNode));

        for (int i = 0; i < this.level1.size(); i++) {
            CDGNode temp = this.level1.get(i);
            temp.setWeight(getWeight(temp));
            temp.setId(i + this.startNum);
        }

        // 删除重复权重的节点
        removeSameWeightNodes(1);

        // 生成指纹
        StringBuilder sb = new StringBuilder();
        for (CDGNode node : this.level1) {
            sb.append(node.getHashString());
        }
        this.level1Finger = DigestUtil.md5Hex(sb.toString());
    }


    private void initLevel2(List<SootClass> appClass) {
        // 根据父类，属性生成依赖关系
        for (int i = 0; i < appClass.size(); i++) {
            SootClass sc = appClass.get(i);
            if (sc.getName().contains("module-info")) {
                continue;
            }
            if (sc.hasSuperclass()) {
                String superClassName = sc.getSuperclass().getName();
                addDependent(this.level2.get(i), superClassName, 2);
            }
            Chain<SootField> fields = sc.getFields();
            for (SootField field : fields) {
                String type = field.getType().toString();
                addDependent(this.level2.get(i), type, 2);
            }
        }

        // 排序
        this.level2.sort((this::compareCDGNode));

        for (int i = 0; i < this.level2.size(); i++) {
            CDGNode temp = this.level2.get(i);
            temp.setWeight(getWeight(temp));
            temp.setId(i + this.startNum);
        }

        // 删除重复权重的节点
        removeSameWeightNodes(2);

        // 生成指纹
        StringBuilder sb = new StringBuilder();
        for (CDGNode node : this.level2) {
            sb.append(node.getHashString());
        }
        this.level2Finger = SecureUtil.md5(sb.toString());
    }

    private void initLevel3(List<SootClass> appClass) {
        // 根据父类，属性，方法生成依赖
        for (int i = 0; i < appClass.size(); i++) {
            SootClass sc = appClass.get(i);
            if (sc.getName().contains("module-info")) {
                continue;
            }
            if (sc.hasSuperclass()) {
                String superClassName = sc.getSuperclass().getName();
                addDependent(this.level3.get(i), superClassName, 3);
            }
            Chain<SootField> fields = sc.getFields();
            for (SootField field : fields) {
                String type = field.getType().toString();
                addDependent(this.level3.get(i), type, 3);
            }
            List<SootMethod> methods = sc.getMethods();
            for (SootMethod sm : methods) {
                String type = sm.getReturnType().toString();
                addDependent(this.level3.get(i), type, 3);
                List<Type> types = sm.getParameterTypes();
                for (Type t : types) {
                    addDependent(this.level3.get(i), t.toString(), 3);
                }
            }
        }

        // 排序
        this.level3.sort((this::compareCDGNode));

        for (int i = 0; i < this.level3.size(); i++) {
            CDGNode temp = this.level3.get(i);
            temp.setWeight(getWeight(temp));
            temp.setId(i + this.startNum);
        }

        // 根据level3更新 classMap 和 allNode
        for (int i = 0; i < this.level3.size(); i++) {
            CDGNode temp = this.level3.get(i);
            this.classMap.put(temp.getClassName(), temp.getId());
            this.allNode.set(temp.getId(), temp);
        }

        // 删除重复权重的节点
        removeSameWeightNodes(3);

        // 生成指纹
        StringBuilder sb = new StringBuilder();
        for (CDGNode node : this.level3) {
            sb.append(node.getHashString());
        }
        this.level3Finger = SecureUtil.md5(sb.toString());
    }


    private int compareCDGNode(CDGNode o1, CDGNode o2) {
        int weight1 = getWeight(o1);
        int weight2 = getWeight(o2);
        if (weight1 != weight2) {
            return weight2 - weight1;
        } else {
            int depSize1 = o1.getDependencies().size();
            int depSize2 = o2.getDependencies().size();
            int beDepSize1 = o1.getBeDependencies().size();
            int beDepSize2 = o2.getBeDependencies().size();
            if ((depSize1 + beDepSize1) != (depSize2 + beDepSize2)) {
                return (depSize2 + beDepSize2) - (depSize1 + beDepSize1);
            } else {
                return depSize2 - depSize1;
            }
        }
    }

    private int getWeight(CDGNode node) {
        int weight = node.getDependencies().size() + node.getBeDependencies().size();
        for (CDGNode n : node.getDependencies()) {
            weight += n.getDependencies().size() + n.getBeDependencies().size();
        }
        return weight;
    }

    private void removeSameWeightNodes(int level) {
        List<CDGNode> nodes = null;
        switch (level) {
            case 1 -> nodes = this.level1;
            case 2 -> nodes = this.level2;
            case 3 -> nodes = this.level3;
        }
        List<CDGNode> removeNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            int weight = getWeight(nodes.get(i));
            int depSize = nodes.get(i).getDependencies().size();
            int beDepSize = nodes.get(i).getBeDependencies().size();
            int index = i + 1;
            boolean flag = false;
            while (
                    index < nodes.size()
                            && getWeight(nodes.get(index)) == weight
                            && nodes.get(index).getDependencies().size() == depSize
                            && nodes.get(index).getBeDependencies().size() == beDepSize
            ) {
                flag = true;
                removeNodes.add(nodes.get(index));
                index++;
            }
            if (flag) {
                removeNodes.add(nodes.get(i));
            }
            i = index - 1;
        }
        for (CDGNode node : removeNodes) {
            removeNode(node, level);
        }
    }

    private void removeNode(CDGNode node, int level) {
        List<CDGNode> nodes = null;
        switch (level) {
            case 1 -> nodes = this.level1;
            case 2 -> nodes = this.level2;
            case 3 -> nodes = this.level3;
        }

        assert nodes != null;
        nodes.remove(node);

        for (CDGNode n : node.getDependencies()) {
            n.removeBeDependent(node);
        }
        for (CDGNode n : node.getBeDependencies()) {
            n.removeDependent(node);
        }
    }

    public Dict toDict() {
        Dict[] level1 = new Dict[this.level1.size()];
        Dict[] level2 = new Dict[this.level2.size()];
        Dict[] level3 = new Dict[this.level3.size()];
        Dict[] allNode = new Dict[this.allNode.size()];

        for (int i = 0; i < this.level1.size(); i++) {
            level1[i] = this.level1.get(i).toDict();
        }
        for (int i = 0; i < this.level2.size(); i++) {
            level2[i] = this.level2.get(i).toDict();
        }
        for (int i = 0; i < this.level3.size(); i++) {
            level3[i] = this.level3.get(i).toDict();
        }
        for (int i = 0; i < this.allNode.size(); i++) {
            allNode[i] = this.allNode.get(i).toDict();
        }

        return Dict.create()
                .set("level1_finger", this.level1Finger)
                .set("level2_finger", this.level2Finger)
                .set("level3_finger", this.level3Finger)
                .set("level1", level1)
                .set("level2", level2)
                .set("level3", level3)
                .set("all_class", allNode);
    }
}
