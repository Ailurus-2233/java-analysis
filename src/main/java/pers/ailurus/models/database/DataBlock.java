package pers.ailurus.models.database;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.ailurus.models.feature.CFGNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataBlock {
    // 唯一标识
    private String md5;

    // 特征信息
    @Alias("stmt_num")
    private int stmtNum;
    @Alias("local_num")
    private int localNum;
    @Alias("invoke_num")
    private int invokeNum;
    @Alias("constant_num")
    private int constantNum;
    private String constants;

    public DataBlock(CFGNode cfgNode) {
        this.md5 = cfgNode.getMd5();
        this.stmtNum = cfgNode.getStmtNum();
        this.localNum = cfgNode.getLocalNum();
        this.invokeNum = cfgNode.getInvokeNum();
        this.constantNum = cfgNode.getConstantNum();
        StringBuilder sb = new StringBuilder();
        for (String s : cfgNode.getConstants()) {
            sb.append(s);
        }
        this.constants = sb.toString();
    }

    public String[] toCSV() {
        return new String[]{
                this.md5,
                String.valueOf(this.stmtNum),
                String.valueOf(this.localNum),
                String.valueOf(this.invokeNum),
                String.valueOf(this.constantNum),
                this.constants
        };
    }
}
