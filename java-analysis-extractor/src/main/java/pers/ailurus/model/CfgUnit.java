package pers.ailurus.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import soot.Unit;

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
public class CfgUnit {
    @JSONField(serialize = false)
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
}
