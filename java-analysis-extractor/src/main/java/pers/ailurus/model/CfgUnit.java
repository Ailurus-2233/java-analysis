package pers.ailurus.model;

import cn.hutool.core.lang.Console;
import fj.P;
import lombok.Data;
import soot.Unit;
import soot.UnitBox;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 控制流图的转换单元
 *
 * @author wzy
 * @date 2022/09/14
 */
@Data
public class CfgUnit implements Serializable {
    private transient Unit unit;
    private int id;
    private int[] child;

    public CfgUnit(Unit unit, int i) {
        this.unit = unit;
        this.id = i;
    }

    public String toString() {
        return String.format("(%d, %s)", this.id, Arrays.toString(this.child));
    }

    public void setChild(List<Integer> child) {
        this.child = child.stream().mapToInt(Integer::intValue).toArray();
    }

    public void setChild(int[] child) {
        this.child = child;
    }

    public void printUnit() {
        Console.print("{}\n", this.unit);
    }
}
