package pers.ailurus;

import cn.hutool.core.lang.Console;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import pers.ailurus.mapper.*;
import pers.ailurus.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


import static pers.ailurus.ObjectGenerator.*;

public class DataOperator {

    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession session;
    private static int execCount = 0;

    private static FeatureFileMapper featureFileMapper;
    private static FeatureClassMapper featureClassMapper;
    private static FeatureMethodMapper featureMethodMapper;
    private static MavenRepositoryMapper mavenRepositoryMapper;
    private static RelationClassMethodMapper relationClassMethodMapper;
    private static RelationFileClassMapper relationFileClassMapper;

    public static void initOperator() throws IOException {



        if (sqlSessionFactory != null) {
            return;
        }
        String resource = "mybatis.xml";

        // 加载MyBatis的主配置文件
        InputStream inputStream = Resources.getResourceAsStream(resource);
        // 通过构建器（SqlSessionFactoryBuilder）构建一个SqlSessionFactory工厂对象
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 获取 SqlSession
        session = sqlSessionFactory.openSession();

        featureFileMapper = session.getMapper(FeatureFileMapper.class);
        featureClassMapper = session.getMapper(FeatureClassMapper.class);
        featureMethodMapper = session.getMapper(FeatureMethodMapper.class);
        mavenRepositoryMapper = session.getMapper(MavenRepositoryMapper.class);
        relationClassMethodMapper = session.getMapper(RelationClassMethodMapper.class);
        relationFileClassMapper = session.getMapper(RelationFileClassMapper.class);

        inputStream.close();
    }

    private static void commitInOperator() {
        if (++execCount % 1000 == 0) {
            session.commit();
        }
//        session.commit();
    }

    public static void close() {
        session.commit();
        session.close();
    }

    public static void insert(FeatureFile ff) {
        try {
            featureFileMapper.insert(ff);
            commitInOperator();
        } catch (Exception ignored) {
        }
    }

    public static void insert(FeatureClass fc) {
        try {
            featureClassMapper.insert(fc);
            commitInOperator();
        } catch (Exception ignored) {
        }
    }

    public static void insert(FeatureMethod fm) {
        try {
            featureMethodMapper.insert(fm);
            commitInOperator();
        } catch (Exception ignored) {
        }
    }

    public static void insert(MavenRepository mr) {
        try {
            mavenRepositoryMapper.insert(mr);
            commitInOperator();
        } catch (Exception ignored) {
        }
    }

    public static void insert(RelationClassMethod rcm) {
        try {
            relationClassMethodMapper.insert(rcm);
            commitInOperator();
        } catch (Exception ignored) {
        }
    }

    public static void insert(RelationFileClass rfc) {
        try {
            relationFileClassMapper.insert(rfc);
            commitInOperator();
        } catch (Exception ignored) {
        }
    }

    public static void insertAnalysisPackage(String groupId, String artifactId, String version, AnalysisPackage ap) throws IOException {
        FeatureFile featureFile = getFileFeatureByAnalysisPackage(groupId, artifactId, version, ap);
        insert(featureFile);
        Set<FeatureClass> fcSet = new HashSet<>();
        Set<FeatureMethod> fmSet = new HashSet<>();
        Set<RelationClassMethod> rcmSet = new HashSet<>();
        Set<RelationFileClass> rfcSet = new HashSet<>();
        for (AnalysisClass ac : ap.getClasses()) {
            FeatureClass fc = getClassFeatureByAnalysisClass(ac);
            fcSet.add(fc);
            rfcSet.add(new RelationFileClass(featureFile.getMd5(), fc.getMd5()));
            for (AnalysisMethod am : ac.getMethods()) {
                FeatureMethod fm = getMethodFeatureByAnalysisMethod(am);
                fmSet.add(fm);
                rcmSet.add(new RelationClassMethod(fc.getMd5(), fm.getMd5()));
            }
        }
        for (FeatureClass fc : fcSet) {
            insert(fc);
        }
        for (FeatureMethod fm : fmSet) {
            insert(fm);
        }
        for (RelationClassMethod rcm : rcmSet) {
            insert(rcm);
        }
        for (RelationFileClass rfc : rfcSet) {
            insert(rfc);
        }
        session.commit();
    }


    public static List<FeatureFile> selectFeatureFileByNumberFeature(FeatureFile featureFile) {
        return featureFileMapper.selectByNumberFeature(featureFile);
    }

    public static List<String> selectFileMd5ByNumberFeature(FeatureFile featureFile) {
        return featureFileMapper.selectMd5ByNumberFeature(featureFile);
    }

    public static List<FeatureClass> selectFeatureClassByNumberFeature(FeatureClass featureClass) {
        return featureClassMapper.selectByNumberFeature(featureClass);
    }

    public static List<String> selectFeatureClassByNumberFeature(FeatureClass featureClass, List<String> ffMd5) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String s : ffMd5) {
            sb.append("'").append(s).append("',");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Map param = new HashMap<>();
        param.put("modifier", featureClass.getModifier());
        param.put("interfaceNum", featureClass.getInterfaceNum());
        param.put("hasSuperClass", featureClass.getHasSuperClass());
        param.put("fieldNum", featureClass.getFieldNum());
        param.put("methodNum", featureClass.getMethodNum());
        param.put("ffMd5", sb.toString());
        param.put("depClassNum", featureClass.getDepClassNum());
        param.put("beDepNum", featureClass.getBeDepNum());

        return featureClassMapper.selectByNumberFeatureWithFileMd5(param);
    }

    public static List<String> selectClassMd5ByMethodMd5(String md5) {
        return relationClassMethodMapper.selectClassMd5ByMethodMd5(md5);
    }

    public static List<String> selectFileMd5ByClassMd5(String key) {
        return relationFileClassMapper.selectFileMd5ByClassMd5(key);
    }

    public static FeatureFile selectFeatureFileByMd5(String md5) {
        return featureFileMapper.selectByPrimaryKey(md5);
    }

    public static List<FeatureClass> selectFeatureClassByFileMd5(String md5) {
        List<String> classMd5List = relationFileClassMapper.selectClassMd5ByFileMd5(md5);
        List<FeatureClass> featureClassList = new ArrayList<>();
        for (String classMd5 : classMd5List) {
            featureClassList.add(featureClassMapper.selectByPrimaryKey(classMd5));
        }
        return featureClassList;
    }

    public static List<String> selectMethodMd5ByClassMd5(String md5) {
        return relationClassMethodMapper.selectMethodMd5ByClassMd5(md5);
    }
}
