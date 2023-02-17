package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;
import pers.ailurus.utils.Extractor;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
import soot.toolkits.graph.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
public class CFGNode {

    private transient Block block;
    private int id;
    private int[] preds;
    private int[] succs;

    private String md5;
    private int stmtNum;
    private int localNum;
    private int invokeNum;
    private int[][] invokeType;

    private String[] constants;
    private int constantNum;

    public CFGNode(Block block, int i) {
        this.block = block;
        this.id = i;
    }

    public String toString() {
        return String.format("(%d, %s)", this.id, Arrays.toString(this.succs));
    }

    public void analysisBlock(FeatureMethod fm, Map<String, Integer> typeMap) {

        this.md5 = DigestUtil.md5Hex(this.block.toString());

        Unit head = this.block.getHead();
        Unit tail = this.block.getTail();

        this.stmtNum = 0;
        this.localNum = 0;
        this.invokeNum = 0;
        List<int[]> invokeType = new ArrayList<>();
        List<String> constant = new ArrayList<>();

        for (Unit u = head; u != tail; u = this.block.getSuccOf(u)) {
            this.stmtNum++;
            for (ValueBox vb : u.getUseAndDefBoxes()) {
                if (vb.getValue() instanceof Local) {
                    this.localNum++;
                }
                if (vb.getValue() instanceof Constant) {
                    this.constantNum++;
                    String temp = vb.getValue().toString();
                    if (temp.startsWith("\"") && temp.endsWith("\""))
                        constant.add(temp.substring(1, temp.length() - 1));
                    else
                        constant.add(temp);
                }
                if (vb.getValue() instanceof InvokeExpr) {
                    this.invokeNum++;
                    int returnType = Extractor.getTypeId(vb.getValue().getType().toString(), typeMap);
                    int[] argsType;
                    String temp = vb.getValue().toString();
                    temp = temp.substring(temp.indexOf("<"), temp.indexOf(">") + 1);
                    if (!temp.contains("("))
                        argsType = new int[1];
                    else {
                        temp = temp.substring(temp.indexOf("(")+1, temp.indexOf(")"));
                        String[] t =  temp.split(",");
                        argsType = new int[t.length+1];
                        for (int i = 0; i < t.length; i++) {
                            argsType[i+1] = Extractor.getTypeId(t[i], typeMap);
                        }
                    }
                    argsType[0] = returnType;
                    invokeType.add(argsType);
                }
            }
        }
        this.invokeType = invokeType.toArray(new int[0][]);
        this.constants = constant.toArray(new String[0]);
    }

    public Dict toDict() {
        return Dict.create()
                .set("stmt_num", this.stmtNum)
                .set("local_num", this.localNum)
                .set("invoke_num", this.invokeNum)
                .set("invoke_type", this.invokeType)
                .set("constants", this.constants)
                .set("constant_num", this.constantNum);
    }
}
