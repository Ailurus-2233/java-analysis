package pers.ailurus.models;

import lombok.Data;

import java.util.List;

@Data
public class Package {
    private String md5;
    private int classNum;
    private int packageDeep;
    private int packageNum;
    private List<Clazz> classes;
    private List<CDGUnit> cdg;
    private String uniMd5;
}
