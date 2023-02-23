package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.ailurus.utils.Analysis;
import soot.SootMethod;
import soot.jimple.*;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BriefBlockGraph;
import tech.tuister.ssdeep4j.FuzzyHashing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureMethod {

    // 根据方法计算的唯一标识
    private String md5;

    // 方法的基本标签
    private int[] base; // [访问修饰符，返回类型]

    // 参数特征
    private int[] param; // [mapping(x) for x in param]

    // 代码特征
    private int[] invoke; // [mapping(x) for x in invoke]
    private String constant; // 常量
    private String cfg; // cfg

    public boolean analysisMethod(SootMethod method, HashMap<String, Integer> classMap) {

        if (!method.isConcrete()) {
            return false;
        }

        JimpleBody body;
        try {
            body = (JimpleBody) method.retrieveActiveBody();
//            this.md5 = DigestUtil.md5Hex(body.toString());
        } catch (Exception e) {
            return false;
        }

        // 基础特征
        this.base = new int[2];
        // 只记录 static, synchronized, native.
        if (method.isStatic()) {
            this.base[0] += 1;
        }
        if (method.isSynchronized()) {
            this.base[0] += 2;
        }
        if (method.isNative()) {
            this.base[0] += 4;
        }
        this.base[1] = Analysis.getTypeId(method.getReturnType().toString(), classMap);

        // 参数特征
        this.param = new int[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            this.param[i] = Analysis.getTypeId(method.getReturnType().toString(), classMap);
        }

        // 代码特征
        ArrayList<Integer> invokeList = new ArrayList<>();
        ArrayList<String> constantList = new ArrayList<>();

        body.getUnits().forEach(unit -> {
            if (unit instanceof InvokeStmt invokeStmt) {
                invokeList.add(Analysis.getTypeId(invokeStmt.getInvokeExpr().getMethod().getDeclaringClass().getName(), classMap));
            } else if (unit instanceof AssignStmt assignStmt) {
                if (assignStmt.getRightOp() instanceof Constant) {
                    String temp = assignStmt.getRightOp().toString();
                    if (temp.startsWith("\"") && temp.endsWith("\""))
                        constantList.add(temp.substring(1, temp.length() - 1));
                    else
                        constantList.add(temp);
                }
            }
        });

        this.invoke = new int[invokeList.size()];
        for (int i = 0; i < invokeList.size(); i++) {
            this.invoke[i] = invokeList.get(i);
        }
        constantList.sort(String::compareTo);
//        this.constant = String.join(",", constantList);
        this.constant = FuzzyHashing.fuzzyHash(String.join(",", constantList).getBytes());

        // cfg
        BriefBlockGraph blocks = new BriefBlockGraph(body);
        StringBuilder cfgBuilder = new StringBuilder();
        ArrayList<Block> blockList = new ArrayList<>(blocks.getBlocks());
        StringBuilder md5Builder = new StringBuilder();
        for (int i = 0; i < blockList.size(); i++) {
            Block block = blockList.get(i);
            List<Integer> succs = new ArrayList<>();
            for (Block succ : block.getSuccs()) {
                succs.add(blockList.indexOf(succ));
            }
            md5Builder.append(block.getBody().toString());
            cfgBuilder.append(String.format("%d:%s;", i, String.join(",", succs.toString())));
        }
        this.cfg = cfgBuilder.toString().replace(" ", "");
        this.md5 = DigestUtil.md5Hex(md5Builder.toString());
//        this.cfg = FuzzyHashing.fuzzyHash(cfgBuilder.toString().getBytes());

        return true;
    }

    public Dict toDict() {
        return Dict.create()
                .set("md5", this.md5)
                .set("base", this.base)
                .set("param", this.param)
                .set("invoke", this.invoke)
                .set("constant", this.constant)
                .set("cfg", this.cfg);
    }

    public Dict toSave() {
        return Dict.create()
                .set("base", this.base)
                .set("param", this.param)
                .set("invoke", this.invoke)
                .set("constant", this.constant)
                .set("cfg", this.cfg);
    }
}
