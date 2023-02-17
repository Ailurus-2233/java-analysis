package pers.ailurus.models.feature;

import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;
import pers.ailurus.utils.Extractor;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BriefBlockGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Data
public class FeatureMethod {

    private String md5;
    private int[] signature;
    private int modifier;
    private int argsNum;
    private int[] argsType;
    private int returnType;
    private String cfgFinger;
    private int localNum;
    private int[] localType;
    private int constantNum;
    private String[] constants;
    private CFG cfg;

    private void initNone() {
        this.constantNum = 0;
        this.constants = new String[0];
        this.localNum = 0;
        this.localType = new int[0];
        this.md5 = DigestUtil.md5Hex("");
        this.cfg = new CFG();
        this.cfgFinger = this.cfg.getFinger();
        StringBuilder sb = new StringBuilder();
        for (int i : this.argsType) {
            sb.append(i);
        }
        this.md5 = DigestUtil.md5Hex(String.format("%s%s%d%d%d%s%d%d", this.cfgFinger, this.md5, this.modifier, this.argsNum, this.returnType, sb, this.localNum, this.constantNum));

    }

    public void setJimpleFeature(SootMethod sm, Map<String, Integer> typeMap) {
        // 设置变量特征
        if (!sm.isConcrete()) {
            initNone();
            return;
        }
        JimpleBody body;
        try {
            body = (JimpleBody) sm.retrieveActiveBody();
        } catch (Exception e) {
            initNone();
            return;
        }
        this.md5 = DigestUtil.md5Hex(body.toString());
        this.localNum = body.getLocalCount();
        List<Local> list = body.getLocals().stream().toList();
        this.localType = new int[this.localNum];
        for (int i = 0; i < this.localNum; i++) {
            this.localType[i] = Extractor.getTypeId(list.get(i).getType().toString(), typeMap);
        }

        // 设置字符串常量特征
        List<String> strings = new ArrayList<>();
        for (Unit unit : body.getUnits()) {
            for (ValueBox box : unit.getUseAndDefBoxes()) {
                if (box.getValue() instanceof Constant) {
                    String temp = box.getValue().toString();
                    if (temp.startsWith("\"") && temp.endsWith("\""))
                        strings.add(temp.substring(1, temp.length() - 1));
                    else
                        strings.add(temp);
                }
            }
        }
        this.constants = strings.toArray(new String[0]);
        this.constantNum = this.constants.length;

        // 设置CFG相关特征
        BriefBlockGraph blocks = new BriefBlockGraph(body);
        this.cfg = new CFG();
        int index = 0;
        for (Block block : blocks) {
            CFGNode cfgNode = new CFGNode(block, index++);
            int[] preds = new int[block.getPreds().size()];
            for (int i = 0; i < preds.length; i++) {
                preds[i] = block.getPreds().get(i).getIndexInMethod();
            }
            int[] succs = new int[block.getSuccs().size()];
            for (int i = 0; i < succs.length; i++) {
                succs[i] = block.getSuccs().get(i).getIndexInMethod();
            }
            cfgNode.setPreds(preds);
            cfgNode.setSuccs(succs);
            cfgNode.analysisBlock(this, typeMap);
            this.cfg.getCfgNodes().add(cfgNode);
        }

        this.cfgFinger = this.cfg.getFinger();
        StringBuilder sb = new StringBuilder();
        for (int i : this.argsType) {
            sb.append(i);
        }
        this.md5 = DigestUtil.md5Hex(String.format("%s%s%d%d%d%s%d%d", this.cfgFinger, this.md5, this.modifier, this.argsNum, this.returnType, sb, this.localNum, this.constantNum));
    }

    public Dict toDict() {
        return Dict.create()
                .set("signature", this.signature)
                .set("modifier", this.modifier)
                .set("args_num", this.argsNum)
                .set("args_type", this.argsType)
                .set("return_type", this.returnType)
                .set("cfg_finger", this.cfgFinger)
                .set("local_num", this.localNum)
                .set("local_type", this.localType)
                .set("constant_num", this.constantNum)
                .set("constants", this.constants)
                .set("cfg", this.cfg.toDict());
    }
}
