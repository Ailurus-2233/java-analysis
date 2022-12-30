package pers.ailurus.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分析得到的包的特征信息
 *
 * @author wzy
 * @date 2022/09/14
 */
@Data
public class AnalysisPackage implements Serializable {
    private String md5;
    private int classNum;
    private int packageDeep;
    private int packageNum;
    private List<AnalysisClass> classes;
    private List<CdgUnit> cdg;
    private String uniMd5;
}
