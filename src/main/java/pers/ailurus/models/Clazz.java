package pers.ailurus.models;

import lombok.Data;
import sootup.core.model.Modifier;

import java.util.List;
import java.util.Set;

@Data
public class Clazz {
    private String md5;
    private Set<Modifier> modifiers;
    private int[] modifiersVector;
    private int interfaceNum;
    private int methodNum;
    private int fieldNum;
    private int isHasSuperClass;
    private int numOfDep;
    private int numOfBeDep;
    private double importance;
    private List<Method> methods;

    private String classType;


    public void modifiersVectorInit() {
        modifiersVector = new int[Modifier.values().length];
        for (Modifier modifier : modifiers) {
            modifiersVector[modifier.ordinal()] = 1;
        }
    }
}
