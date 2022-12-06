package pers.ailurus;

import org.apache.commons.codec.digest.DigestUtils;
import pers.ailurus.model.*;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.toolkits.graph.ClassicCompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static pers.ailurus.MyFileUtil.getFileMd5;

public class Extractor {

    public static AnalysisPackage extract(String filePath, int limitSecond) throws TimeoutException {
        Callable<AnalysisPackage> task = () -> extract(filePath);
        ExecutorService exeServices = Executors.newSingleThreadExecutor();
        Future<AnalysisPackage> future = exeServices.submit(task);
        try {
            return future.get(limitSecond, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(true);
            throw new TimeoutException("Timeout");
        } finally {
            exeServices.shutdown();
        }
    }

    public static AnalysisPackage extract(String filePath) {
        if (!MyFileUtil.isJarFile(filePath)) {
            throw new RuntimeException("Not jar file");
        }
        AnalysisPackage ap = new AnalysisPackage();
        String target = MyFileUtil.getExtractTargetPath(filePath);
        if (MyFileUtil.extractJarFile(filePath, target)) {
            // 解析解压结果
            ap = analysisPackage(target);
            try {
                ap.setMd5(MyFileUtil.getFileMd5(filePath));
            } catch (IOException e) {
                throw new RuntimeException("Get md5 error, check your file path");
            }
        }
        return ap;
    }


    /**
     * 设置 Soot 全局文件夹路径
     *
     * @param packageDir 待分析已解压包路径
     */
    public static void setupSoot(String packageDir) {
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(List.of(packageDir));
        Scene.v().loadNecessaryClasses();
    }


    public static void resetSoot() {
        G.reset();
    }

    /**
     * 获取方法的控制流图
     *
     * @param sm 待分析的 SootMethod 对象
     * @return {@code List<CfgUnit>}
     * @throws RuntimeException 运行时异常
     */
    public static List<CfgUnit> getCfg(SootMethod sm) {

        try {
            JimpleBody body = (JimpleBody) sm.retrieveActiveBody();
            UnitGraph graph = new ClassicCompleteUnitGraph(body);
            Map<Unit, Integer> unitMap = new HashMap<>(body.getUnits().size());
            List<CfgUnit> cfg = new ArrayList<>();
            int i = 0;
            for (Unit unit : graph) {
                cfg.add(new CfgUnit(unit, i));
                unitMap.put(unit, i++);
            }
            for (CfgUnit unit : cfg) {
                List<Integer> child = new ArrayList<>();
                for (Unit succ : graph.getSuccsOf(unit.getUnit())) {
                    child.add(unitMap.get(succ));
                }
                unit.setChild(child);
            }
            return cfg;
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * 得到 cfg 指纹
     *
     * @param cfg 控制流图结构
     * @return {@code String}
     */
    public static String getCfgFinger(List<CfgUnit> cfg) {
        StringBuilder sb = new StringBuilder();
        if (cfg != null) {
            for (CfgUnit unit : cfg) {
                sb.append(unit.toString());
            }
        }
        return DigestUtils.md5Hex(sb.toString());
    }

    /**
     * 根据 SootClass 分析类特征信息
     *
     * @param sc 待分析 SootClass
     * @return {@code AnalysisClass}
     */
    public static AnalysisClass analysisClass(SootClass sc, String packageDir) {
        AnalysisClass ac = new AnalysisClass();
        ac.setMd5(getClassMd5(sc, packageDir));
        ac.setClassType(sc.getType().toString());
        ac.setModifier(sc.getModifiers());
        ac.setFieldNum(sc.getFieldCount());
        ac.setMethodNum(sc.getMethodCount());
        ac.setInterfaceNum(sc.getInterfaceCount());
        ac.setIsHasSuperClass("java.lang.Object".equals(sc.getSuperclass().getName()) ? 0 : 1);
        return ac;
    }

    /**
     * 根据 SootMethod 分析方法特征信息
     *
     * @param sm 待分析 SootMethod
     * @return {@code AnalysisMethod}
     */
    public static AnalysisMethod analysisMethod(SootMethod sm) {
        AnalysisMethod am = new AnalysisMethod();
        am.setModifier(sm.getModifiers());
        am.setReturnType(sm.getReturnType().toString());
        am.setArgsNum(sm.getParameterTypes().size());
        am.setCfg(getCfg(sm));
        am.setCfgFinger(getCfgFinger(am.getCfg()));
        am.genMd5();
        return am;
    }

    /**
     * 根据解压后的包文件路径解析包的特征
     *
     * @param packageDir 待分析的包路径
     * @return {@code AnalysisPackage}
     */
    public static AnalysisPackage analysisPackage(String packageDir) {
        setupSoot(packageDir);
        List<SootClass> classChain = Scene.v().getApplicationClasses().stream().toList();
        AnalysisPackage ap = new AnalysisPackage();
        ap.setClassNum(classChain.size());
        Set<String> packageSet = new HashSet<>();
        List<AnalysisClass> acList = new ArrayList<>(ap.getClassNum());

        List<CdgUnit> cdg = getCdg(classChain);
        ap.setCdg(cdg);
        Map<String, Integer> dep = getDepMap(cdg);
        Map<String, Integer> beDep = getBeDepMap(cdg);

        for (SootClass sc : classChain) {
            // 设置包深度
            int packageDeep = sc.getJavaPackageName().split("\\.").length;
            if (packageDeep > ap.getPackageDeep()) {
                ap.setPackageDeep(packageDeep);
            }

            packageSet.add(sc.getJavaPackageName());
            AnalysisClass ac = analysisClass(sc, packageDir);
            List<AnalysisMethod> amList = new ArrayList<>(sc.getMethodCount());
            for (SootMethod sm : sc.getMethods()) {
                amList.add(analysisMethod(sm));
            }
            ac.setMethods(amList);
            ac.setNumOfDep(dep.get(ac.getClassType()));
            ac.setNumOfBeDep(beDep.get(ac.getClassType()));

            // 修改class的MD5 改为 classType + cdg的依赖关系
            ac.setMd5(DigestUtils.md5Hex(ac.getMd5() + ac.getNumOfDep() + ac.getNumOfBeDep()));
            acList.add(ac);
        }
        ap.setPackageNum(packageSet.size());
        ap.setClasses(acList);
        return ap;
    }


    /**
     * 根据类列表获取类依赖图
     *
     * @param classes 待分析的类列表
     * @return {@code List<CdgUnit>}
     */
    public static List<CdgUnit> getCdg(List<SootClass> classes) {
        List<CdgUnit> cdg = new ArrayList<>();
        Map<String, Integer> classMap = new HashMap<>(classes.size());
        int i = 0;
        for (SootClass sc : classes) {
            classMap.put(sc.getName(), i);
            cdg.add(new CdgUnit(sc.getName(), i++));
        }
        for (SootClass sc : classes) {
            // 从父类，属性，方法返回值，方法中单元左值类型获取依赖信息
            if ("module-info".equals(sc.getName())) {
                continue;
            }
            Set<String> temp = new HashSet<>();
            temp.add(sc.getSuperclass().getName());
            Chain<SootField> fields = sc.getFields();
            for (SootField sf: fields) {
                temp.add(sf.getType().toString());
            }
            for (SootMethod sm : sc.getMethods()) {
                temp.add(sm.getReturnType().toString());
                for (Type type : sm.getParameterTypes()) {
                    temp.add(type.toString());
                }
                if (!sm.hasActiveBody()) {
                    continue;
                }
                JimpleBody body = (JimpleBody) sm.retrieveActiveBody();
                for (Unit unit : body.getUnits()) {
                    if (unit instanceof DefinitionStmt def) {
                        temp.add(def.getLeftOp().getType().toString());
                    }
                }
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

    public static Map<String, Integer> getDepMap(List<CdgUnit> cdg) {
        Map<String, Integer> dep = new HashMap<>(cdg.size());
        for (CdgUnit c: cdg) {
            dep.put(c.getClassName(), c.getDependencies().length);
        }
        return dep;
    }

    public static Map<String, Integer> getBeDepMap(List<CdgUnit> cdg) {
        Map<String, Integer> beDep = new HashMap<>(cdg.size());
        for (CdgUnit c: cdg) {
            beDep.put(c.getClassName(), 0);
        }
        for (CdgUnit c: cdg) {
            for (Integer i : c.getDependencies()) {
                String name = cdg.get(i).getClassName();
                beDep.put(name, beDep.get(name) + 1);
            }
        }
        return beDep;
    }

    public static String getClassMd5(SootClass sc, String packageDir) {
        String classPath = sc.getName().replace(".", "/") + ".class";
        String classFile = packageDir + "/" + classPath;
        try {
            return getFileMd5(classFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
