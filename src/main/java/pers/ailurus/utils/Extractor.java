package pers.ailurus.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import org.apache.commons.codec.digest.DigestUtils;
import pers.ailurus.models.feature.CDG;
import pers.ailurus.models.feature.FeatureClass;
import pers.ailurus.models.feature.FeatureMethod;
import pers.ailurus.models.feature.FeaturePackage;
import soot.*;
import soot.options.Options;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.util.Chain;

import java.util.*;

public class Extractor {

    public static void initSoot(String path) {
        G.reset();
        Options.v().set_whole_program(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(List.of(path));
        Scene.v().loadNecessaryClasses();
    }


    public static FeaturePackage analysisPackage(String path) {

        // 解压jar
//        String target = FileOperator.getExtractTargetPath(path);
//        FileOperator.extractJarFile(path, target);

        // 初始化 soot
        initSoot(path);
        FeaturePackage result = new FeaturePackage();

        // 获取所有类的编号
        Map<String, Integer> typeMap = new HashMap<>();
        Chain<SootClass> classChain = Scene.v().getClasses();
        Chain<SootClass> appClassChain = Scene.v().getApplicationClasses();

        List<SootClass> classList = new ArrayList<>(classChain);
        List<SootClass> appClassList = new ArrayList<>(appClassChain);

        CDG cdg = new CDG(appClassList);

        // 根据重要性排序
        classList.sort((o1, o2) -> {
            int o1Importance = getClassImportance(o1, cdg, appClassList);
            int o2Importance = getClassImportance(o2, cdg, appClassList);
            return o2Importance - o1Importance;
        });

        int index = 0;
        Map<Integer, String> temp = new HashMap<>();
        for (SootClass sc : classList) {
            typeMap.put(sc.getName(), index++);
            temp.put(index, sc.getName());
        }

        // 解析包级特征
        result.setClassNum(appClassList.size());

        Set<String> packageSet = new HashSet<>();
        List<FeatureClass> classes = new ArrayList<>(appClassList.size());

        result.setCdg(cdg);

        FileOperator.writeFile(JSONUtil.toJsonPrettyStr(cdg.toDict()), path + ".cdg.json");

        List<String> classMd5 = new ArrayList<>(appClassList.size());

        int methodNum = 0;
        int fieldNum = 0;

        for (SootClass sc : appClassList) {
            if (sc.getName().contains("module-info")) {
                result.setClassNum(result.getClassNum() - 1);
                continue;
            }
            // 设置包深度
            int packageDeep = sc.getJavaPackageName().split("\\.").length;
            if (packageDeep > result.getPackageDeep()) {
                result.setPackageDeep(packageDeep);
            }
            packageSet.add(sc.getJavaPackageName());
            FeatureClass clazz = analysisClass(sc, result.getCdg(), typeMap);
            classMd5.add(clazz.getMd5());

            methodNum += clazz.getMethodNum();
            fieldNum += clazz.getFieldNum();

            classes.add(clazz);
        }
        String[] md5 = classMd5.toArray(new String[0]);
        Arrays.sort(md5);

        result.setMd5(DigestUtils.md5Hex(String.join("", md5)));

        result.setPackageNum(packageSet.size());
        result.setClasses(classes);

        result.setMethodNum(methodNum);
        result.setFieldNum(fieldNum);

        return result;
    }

    public static FeatureClass analysisClass(SootClass sc, CDG cdg, Map<String, Integer> type) {
        FeatureClass clazz = new FeatureClass();

        // 基础信息
        clazz.setClassId(type.get(sc.getName()));
        clazz.setPackageDeep(sc.getJavaPackageName().split("\\.").length);
        clazz.setClassType(sc.getName());
        clazz.setModifiers(sc.getModifiers());
        clazz.setFieldNum(sc.getFieldCount());
        clazz.setMethodNum(sc.getMethodCount());
        clazz.setInterfaceNum(sc.getInterfaceCount());
        clazz.setHasSuperClass("java.lang.Object".equals(sc.getSuperclass().getName()) ? 0 : 1);

        int[] fieldTypes = new int[clazz.getFieldNum()];
        int[] methodTypes = new int[clazz.getMethodNum()];
        List<SootField> fields = sc.getFields().stream().toList();
        for (int i = 0; i < clazz.getFieldNum(); i++) {
            fieldTypes[i] = getTypeId(fields.get(i).getType().toString(), type);
        }
        clazz.setFieldType(fieldTypes);

        // 依赖关系
        clazz.setDepNum(cdg.getDep().get(clazz.getClassType()));
        clazz.setBeDepNum(cdg.getBeDep().get(clazz.getClassType()));

        // 注解信息
        List<Tag> tags = sc.getTags();
        int annotationNum = 0;
        for (Tag tag : tags) {
            if (tag instanceof VisibilityAnnotationTag vat) {
                annotationNum = vat.getAnnotations().size();
                break;
            }
        }
        clazz.setAnnotationNum(annotationNum);

        // 方法特征分析
        List<FeatureMethod> methods = new ArrayList<>(sc.getMethodCount());
        List<SootMethod> sms = sc.getMethods().stream().toList();
        String[] methodMd5 = new String[sc.getMethodCount()];
        for (int i = 0; i < sms.size(); i++) {
            methodTypes[i] = getTypeId(sms.get(i).getReturnType().toString(), type);
            FeatureMethod fm = analysisMethod(sms.get(i), type);
            methods.add(fm);
            methodMd5[i] = fm.getMd5();
        }
        clazz.setMethods(methods);
        clazz.setMethodType(methodTypes);
        Arrays.sort(methodMd5);
        clazz.setMd5(DigestUtils.md5Hex(String.join("", methodMd5)));
        return clazz;
    }

    public static FeatureMethod analysisMethod(SootMethod sm, Map<String, Integer> type) {
        FeatureMethod method = new FeatureMethod();
        method.setModifier(sm.getModifiers());
        method.setReturnType(getTypeId(sm.getReturnType().toString(), type));
        method.setArgsNum(sm.getParameterCount());
        int[] argsType = new int[method.getArgsNum()];
        List<Type> types = sm.getParameterTypes();
        for (int i = 0; i < method.getArgsNum(); i++) {
            argsType[i] = getTypeId(types.get(i).toString(), type);
        }
        method.setArgsType(argsType);
        method.setJimpleFeature(sm, type);
        return method;
    }

    public static Dict getJimpleMd5(String path) {
        Dict result = Dict.create();
        initSoot(path);
        Dict md5 = Dict.create();
        Dict body = Dict.create();
        List<SootClass> list = Scene.v().getApplicationClasses().stream().toList();
        for (SootClass sc : list) {
            for (SootMethod sm : sc.getMethods()) {
                if (sm.isConcrete()) {
                    body.put(sm.getSignature(), sm.retrieveActiveBody().toString());
                    md5.put(sm.getSignature(), DigestUtils.md5Hex(sm.retrieveActiveBody().toString()));
                } else {
                    body.put(sm.getSignature(), "");
                    md5.put(sm.getSignature(), "");
                }
            }
        }
        result.put("md5", md5);
        result.put("body", body);
        return result;
    }

    private static final Map<String, String> baseToClass = new HashMap<>();

    static {
        baseToClass.put("void", "java.lang.Void");
        baseToClass.put("boolean", "java.lang.Boolean");
        baseToClass.put("byte", "java.lang.Byte");
        baseToClass.put("short", "java.lang.Short");
        baseToClass.put("int", "java.lang.Integer");
        baseToClass.put("long", "java.lang.Long");
        baseToClass.put("float", "java.lang.Float");
        baseToClass.put("double", "java.lang.Double");
        baseToClass.put("char", "java.lang.Character");
    }

    public static int getTypeId(String type, Map<String, Integer> typeMap) {
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        } else {
            if (baseToClass.containsKey(type))
                return typeMap.get(baseToClass.get(type));
            else
                return -1;
        }
    }

    public static int getClassImportance(SootClass clazz, CDG cdg, List<SootClass> appClasses) {
        // 重要性 = 属性数量+方法数量+接口数量+注解数量+依赖数量+被依赖数量
        if (clazz.getName().contains("module-info")) {
            return -1;
        }
        if (!appClasses.contains(clazz)) {
            return 0;
        }
        int importance = clazz.getFieldCount() + clazz.getMethodCount() + clazz.getInterfaceCount();
        List<Tag> tags = clazz.getTags();
        for (Tag tag : tags) {
            if (tag instanceof VisibilityAnnotationTag vat) {
                importance += vat.getAnnotations().size();
                break;
            }
        }
        importance += cdg.getDep().get(clazz.getName());
        importance += cdg.getBeDep().get(clazz.getName());
        return importance;
    }

}