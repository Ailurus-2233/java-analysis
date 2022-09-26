package pers.ailurus.model;

import lombok.Data;

import java.util.List;

/**
 * 分析得到的类的特征信息
 *
 * @author wzy
 * @date 2022/09/14
 */
@Data
public class AnalysisClass {

    private String md5;
    private int modifier;
    private int interfaceNum;
    private int methodNum;
    private int fieldNum;
    private int isHasSuperClass;
    private int numOfDep;
    private int numOfBeDep;
    private double importance;
    private List<AnalysisMethod> methods;

    private String classType;
}