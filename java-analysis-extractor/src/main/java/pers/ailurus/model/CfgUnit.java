package pers.ailurus.model;

import lombok.Data;
import soot.Unit;

import java.util.List;

/**
 * 控制流图的转换单元
 *
 * @author wzy
 * @date 2022/09/14
 */
@Data
public class CfgUnit {
    private Unit unit;
    private int id;
    private List<Integer> child;

    public CfgUnit(Unit unit, int i) {
        this.unit = unit;
        this.id = i;
    }

    public String toString() {
        return String.format("(%d, %s)", this.id, this.child.toString());
    }
}
