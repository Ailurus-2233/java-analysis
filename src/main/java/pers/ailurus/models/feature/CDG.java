package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import lombok.Data;
import soot.*;
import soot.jimple.*;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.util.Chain;

import java.util.*;

@Data
public class CDG {

    private List<CDGNode> cdgNodes;
    private Map<String, Integer> classMap;
    private Map<String, Integer> dep;
    private Map<String, Integer> beDep;

    public CDG(List<SootClass> classes) {
        this.cdgNodes = new ArrayList<>();
        this.classMap = new HashMap<>(classes.size());
        int i = 0;
        for (SootClass sc : classes) {
            if (sc.getName().contains("module-info")) {
                continue;
            }
            this.classMap.put(sc.getName(), i);
            this.cdgNodes.add(new CDGNode(sc.getName(), i++));
        }
        for (SootClass sc : classes) {
            // 从 父类类型，接口类型，属性类型，方法返回值类型，方法参数类型，定义语句中左值类型，调用语句定义类类型 获取依赖信息
            if (sc.getName().contains("module-info")) {
                continue;
            }
            Set<String> temp = new HashSet<>();

            // 父类类型
            temp.add(sc.getSuperclass().getName());

            // 接口类型
            for (SootClass s : sc.getInterfaces()) {
                temp.add(s.getName());
            }

            // 属性类型
            for (SootField sf : sc.getFields()) {
                temp.add(sf.getType().toString());
            }

            // 方法返回值类型，参数类型，定义语句中左值类型，调用语句定义类类型
            int constantCount = 0;
            for (SootMethod sm : sc.getMethods()) {
                temp.add(sm.getReturnType().toString());
                for (Type type : sm.getParameterTypes()) {
                    temp.add(type.toString());
                }
                if (sm.isConcrete()) {
                    JimpleBody body = (JimpleBody) sm.retrieveActiveBody();
                    Chain<Unit> units = body.getUnits();
                    for (Unit unit : units) {
                        // 常量数量
                        for (ValueBox box : unit.getUseAndDefBoxes()) {
                            if (box.getValue() instanceof Constant) {
                                constantCount++;
                            }
                        }
                        if (unit instanceof DefinitionStmt def) {
                            temp.add(def.getLeftOp().getType().toString());
                            if (def.getRightOp() instanceof InvokeExpr invoke) {
                                temp.add(invoke.getMethod().getDeclaringClass().getName());
                            }
                        } else if (unit instanceof InvokeStmt invoke) {
                            temp.add(invoke.getInvokeExpr().getMethod().getDeclaringClass().getName());
                        }
                    }
                }
            }
            List<Integer> dep = new ArrayList<>();
            for (String s : temp) {
                if (this.classMap.containsKey(s) && !s.equals(sc.getName())) {
                    dep.add(this.classMap.get(s));
                }
            }

            CDGNode cdgNode = this.cdgNodes.get(this.classMap.get(sc.getName()));

            // 类型重要性 = 接口数量 + 注解数量
            int typeImportance = sc.getInterfaceCount();
            for (Tag tag : sc.getTags()) {
                if (tag instanceof VisibilityAnnotationTag vat) {
                    typeImportance += vat.getAnnotations().size();
                    break;
                }
            }
            // 内容重要性 = 属性数量 + 方法数量 + 常量数量
            int contentImportance = sc.getFieldCount() + sc.getMethodCount() + constantCount;


            cdgNode.setDependents(dep.stream().mapToInt(Integer::intValue).toArray());
            cdgNode.setTypeImportance(typeImportance);
            cdgNode.setContentImportance(contentImportance);
        }

        this.dep = new HashMap<>(this.cdgNodes.size());
        for (CDGNode c : this.cdgNodes) {
            dep.put(c.getClassName(), c.getDependents() == null ? 0 : c.getDependents().length);
        }

        this.beDep = new HashMap<>(this.cdgNodes.size());
        HashMap<String, ArrayList<Integer>> beDepMap = new HashMap<>(this.cdgNodes.size());
        for (CDGNode c : this.cdgNodes) {
            beDep.put(c.getClassName(), 0);
            beDepMap.put(c.getClassName(), new ArrayList<>());
        }

        for (CDGNode c : this.cdgNodes) {
            for (Integer j : c.getDependents()) {
                String name = this.cdgNodes.get(j).getClassName();
                beDep.put(name, beDep.get(name) + 1);
                beDepMap.get(name).add(c.getId());
            }
        }
        for (CDGNode c : this.cdgNodes) {
            c.setBeDependents(beDepMap.get(c.getClassName()).stream().mapToInt(Integer::intValue).toArray());

            // 结构重要性 = 依赖数量 + 被依赖数量
            int structImportance = dep.get(c.getClassName()) + beDep.get(c.getClassName());
            c.setStructImportance(structImportance);
            c.setImportance(structImportance + c.getTypeImportance() + c.getContentImportance());
        }

        // 依次根据重要性，结构重要性，内容重要性，类型重要性排序
        this.cdgNodes.sort((o1, o2) -> {
            if (o1.getImportance() != o2.getImportance()) {
                return o2.getImportance() - o1.getImportance();
            } else if (o1.getStructImportance() != o2.getStructImportance()) {
                return o2.getStructImportance() - o1.getStructImportance();
            } else if (o1.getContentImportance() != o2.getContentImportance()) {
                return o2.getContentImportance() - o1.getContentImportance();
            } else {
                return o2.getTypeImportance() - o1.getTypeImportance();
            }
        });

        // 重新设置id
        HashMap<Integer, Integer> oldToNewMap = new HashMap<>(this.cdgNodes.size());
        for (i = 0; i < this.cdgNodes.size(); i++) {
            oldToNewMap.put(this.cdgNodes.get(i).getId(), i);
        }
        for (CDGNode c : this.cdgNodes) {
            c.changeIdByMap(oldToNewMap);
            this.classMap.put(c.getClassName(), c.getId());
        }
    }

    public Dict toDict() {
        Dict dict = Dict.create()
                .set("node_num", this.cdgNodes.size())
                .set("class_map", this.classMap)
                .set("dep_num", this.dep)
                .set("be_dep_num", this.beDep);
        List<Dict> nodes = new ArrayList<>(this.cdgNodes.size());
        for (CDGNode c : this.cdgNodes) {
            nodes.add(c.toDict());
        }
        dict.set("nodes", nodes);
        return dict;
    }
}
