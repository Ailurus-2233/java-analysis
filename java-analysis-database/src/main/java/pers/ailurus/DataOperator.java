package pers.ailurus;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.ailurus.mapper.*;
import pers.ailurus.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static pers.ailurus.ObjectGenerator.*;

public class DataOperator {

    private static Logger logger = LoggerFactory.getLogger(DataOperator.class);
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

    public static void insertAnalysisPackage(String name, String version, AnalysisPackage ap) throws IOException {
        FeatureFile featureFile = getFileFeatureByAnalysisPackage(name, version, ap);
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

    public static List<FeatureClass> selectFeatureClassByNumberFeature(FeatureClass featureClass) {
        return featureClassMapper.selectByNumberFeature(featureClass);
    }

    public static List<String> selectClassMd5ByMethodMd5(String md5) {
        return relationClassMethodMapper.selectClassMd5ByMethodMd5(md5);
    }

    public static List<String> selectFileMd5ByClassMd5(String key) {
        return relationFileClassMapper.selectFileMd5ByClassMd5(key);
    }

    public static List<MavenRepository> selectMavenDownloadList(int count) {
        return mavenRepositoryMapper.selectMavenDownloadList(count);
    }
}
