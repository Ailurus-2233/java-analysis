package pers.ailurus.models.feature;

import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CFG {
    private List<CFGNode> cfgNodes;

    public CFG() {
        this.cfgNodes = new ArrayList<>();
    }

    public String getFinger() {
        StringBuilder sb = new StringBuilder("");
        for (CFGNode cfgNode : cfgNodes) {
            sb.append(cfgNode.toString());
        }
        return DigestUtil.md5Hex(sb.toString());
    }

    public Dict toDict() {
        Dict dicts = Dict.create();
        for (int i = 0; i < cfgNodes.size(); i++) {
            dicts.set(cfgNodes.get(i).getMd5(), cfgNodes.get(i).toDict());
        }
        return dicts;
    }
}
