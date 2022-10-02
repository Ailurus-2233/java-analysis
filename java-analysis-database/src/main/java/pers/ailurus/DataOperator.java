package pers.ailurus;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import pers.ailurus.mapper.*;
import pers.ailurus.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static pers.ailurus.ObjectGenerator.*;

public class DataOperator {

    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession session;
    private static int execCount;

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
        if (execCount++ % 1000 == 0) {
            session.commit();
        }
//        session.commit();
    }

    public static void commit() {
        session.commit();
    }

    public static void close() {
        session.commit();
        session.close();
    }

}
