package pers.ailurus.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.DigestUtil;
import fj.Unit;
import fj.data.Java;
import pers.ailurus.models.CDGUnit;
import pers.ailurus.models.Clazz;
import pers.ailurus.models.Method;
import pers.ailurus.models.Package;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.core.model.SootClass;
import sootup.core.model.SootField;
import sootup.core.model.SootMethod;
import sootup.core.types.Type;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;

import java.io.IOException;
import java.util.*;


public class Extractor {

    public static JavaProject project;
    public static JavaView view;

    public static void initSootup(String path, int javaVersion) {
        AnalysisInputLocation<JavaSootClass> inputLocation = new JavaClassPathAnalysisInputLocation(path);
        JavaLanguage language = new JavaLanguage(javaVersion);
        project = JavaProject.builder(language).addInputLocation(inputLocation).build();
        view = project.createFullView();
    }

    public static Package extract(String path) {
        if (!FileOperator.isJarFile(path)) {
            throw new RuntimeException("Not jar file");
        }
        Package analysisPackage = new Package();
        String target = FileOperator.getExtractTargetPath(path);
        if (FileOperator.extractJarFile(path, target)) {
            // 解析解压结果
            analysisPackage = analysisPackage(target);
            analysisPackage.setMd5(execUniMd5(analysisPackage));
        }
        return analysisPackage;
    }

    public static Package analysisPackage(String target) {
        initSootup(target, 17);
        Collection<JavaSootClass> classChain = view.getClasses();
        Package result = new Package();
        result.setClassNum(classChain.size());
        Set<String> packageSet = new HashSet<>();
        List<Clazz> classList = new ArrayList<>(result.getClassNum());

        List<CDGUnit> cdg = getCdg(classChain);
        result.setCdg(cdg);
        Map<String, Integer> dep = getDepMap(cdg);
        Map<String, Integer> beDep = getBeDepMap(cdg);
        for (SootClass sc : classChain) {
            if ("module-info".equals(sc.getName())) continue;
            // 设置包深度
            String type = sc.getType().toString();

            int packageDeep = type.split(".").length - 1;
            if (packageDeep > result.getPackageDeep()) {
                result.setPackageDeep(packageDeep);
            }
            Console.print("{}\n", type);
            int temp = type.lastIndexOf(".");
            if (temp == -1) {
                packageSet.add("");
            } else {
                packageSet.add(type.substring(0, temp));
            }
            Clazz ac = analysisClass(sc);
            List<Method> amList = new ArrayList<>(sc.getMethods().size());
            Set<?extends SootMethod> methods = sc.getMethods();
            for (SootMethod sm : methods) {
//                amList.add(analysisMethod(sm));
            }
            ac.setMethods(amList);
            ac.setNumOfDep(dep.get(ac.getClassType()));
            ac.setNumOfBeDep(beDep.get(ac.getClassType()));

            // 修改class的MD5 改为 classType + cdg的依赖关系
//            ac.setMd5(DigestUtil.md5Hex(ac.getMd5() + ac.getNumOfDep() + ac.getNumOfBeDep()));
            classList.add(ac);
        }
        result.setPackageNum(packageSet.size());
        result.setClasses(classList);

        return result;
    }

    private static Clazz analysisClass(SootClass sc) {
        Clazz ac = new Clazz();
        ac.setMd5(getClassMd5(sc));
        ac.setClassType(sc.getType().toString());
        ac.setModifiers(sc.getModifiers());
        ac.modifiersVectorInit();
        ac.setFieldNum(sc.getFields().size());
        ac.setMethodNum(sc.getMethods().size());
        ac.setInterfaceNum(sc.getInterfaces().size());
        ac.setIsHasSuperClass("java.lang.Object".equals(sc.getSuperclass().get().toString()) ? 0 : 1);
        return ac;
    }

    private static String getClassMd5(SootClass sc) {
        String classFile = sc.getClassSource().toString();
        try {
            return FileOperator.getFileMd5(classFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Integer> getBeDepMap(List<CDGUnit> cdg) {
        Map<String, Integer> beDep = new HashMap<>(cdg.size());
        for (CDGUnit c : cdg) {
            beDep.put(c.getClassName(), 0);
        }
        for (CDGUnit c : cdg) {
            for (Integer i : c.getDependencies()) {
                String name = cdg.get(i).getClassName();
                beDep.put(name, beDep.get(name) + 1);
            }
        }
        return beDep;
    }

    private static Map<String, Integer> getDepMap(List<CDGUnit> cdg) {
        Map<String, Integer> dep = new HashMap<>(cdg.size());
        for (CDGUnit c : cdg) {
            dep.put(c.getClassName(), c.getDependencies() == null ? 0 : c.getDependencies().length);
        }
        return dep;
    }

    private static List<CDGUnit> getCdg(Collection<JavaSootClass> classes) {
        List<CDGUnit> cdg = new ArrayList<>();
        Map<String, Integer> classMap = new HashMap<>(classes.size());
        int i = 0;
        for (SootClass sc : classes) {
            if ("module-info".equals(sc.getName())) {
                continue;
            }
            classMap.put(sc.getName(), i);
            cdg.add(new CDGUnit(sc.getName(), i++));
        }
        for (SootClass sc : classes) {
            // 从父类，属性，方法返回值，方法中单元左值类型获取依赖信息
            if ("module-info".equals(sc.getName())) {
                continue;
            }
            Set<String> temp = new HashSet<>();
            temp.add(sc.getSuperclass().toString());
            Set<? extends SootField> fields = sc.getFields();
            for (SootField sf : fields) {
                temp.add(sf.getType().toString());
            }
            List<Integer> dep = new ArrayList<>();
            for (String s : temp) {
                if (classMap.containsKey(s) && !s.equals(sc.getName())) {
                    dep.add(classMap.get(s));
                }
            }
            cdg.get(classMap.get(sc.getName())).setDependencies(dep);
        }
        return cdg;
    }


    private static String execUniMd5(Package analysisPackage) {
        StringBuilder sb = new StringBuilder();
        for (Clazz ac : analysisPackage.getClasses()) {
            sb.append(ac.getMd5());
        }
        return DigestUtil.md5Hex(sb.toString());
    }

}
