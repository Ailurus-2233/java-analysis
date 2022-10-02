package pers.ailurus;

import org.apache.ibatis.session.SqlSession;

import pers.ailurus.mapper.*;
import pers.ailurus.model.*;


import java.io.IOException;


import static pers.ailurus.ObjectGenerator.*;

public class MPDataOperator {

    private static final SqlSession session;

    private static final FeatureFileMapper featureFileMapper;
    private static final FeatureClassMapper featureClassMapper;
    private static final FeatureMethodMapper featureMethodMapper;
    private static final MavenRepositoryMapper mavenRepositoryMapper;
    private static final RelationClassMethodMapper relationClassMethodMapper;
    private static final RelationFileClassMapper relationFileClassMapper;

    static {
        try {
            session = MPConfig.before();
            featureFileMapper = session.getMapper(FeatureFileMapper.class);
            featureClassMapper = session.getMapper(FeatureClassMapper.class);
            featureMethodMapper = session.getMapper(FeatureMethodMapper.class);
            mavenRepositoryMapper = session.getMapper(MavenRepositoryMapper.class);
            relationClassMethodMapper = session.getMapper(RelationClassMethodMapper.class);
            relationFileClassMapper = session.getMapper(RelationFileClassMapper.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertAnalysisPackage(String name, String version, AnalysisPackage ap) throws IOException {
        FeatureFile featureFile = getFileFeatureByAnalysisPackage(name, version, ap);
        insert(featureFile);
        for (AnalysisClass ac : ap.getClasses()) {
            FeatureClass fc = getClassFeatureByAnalysisClass(ac);
            insert(fc);
            insert(new RelationFileClass(featureFile.getMd5(), fc.getMd5()));
            for (AnalysisMethod am : ac.getMethods()) {
                FeatureMethod fm = getMethodFeatureByAnalysisMethod(am);
                insert(fm);
                insert(new RelationClassMethod(fc.getMd5(), fm.getMd5()));
            }
        }
        session.commit();
    }

    public static void insert(FeatureFile ff) {
        try {
            featureFileMapper.insert(ff);
        } catch (Exception e) {
        }
    }

    public static void insert(FeatureClass fc) {
        try {
            featureClassMapper.insert(fc);
        } catch (Exception e) {
        }
    }

    public static void insert(FeatureMethod fm) {
        try {
            featureMethodMapper.insert(fm);
        } catch (Exception e) {
        }
    }

    public static void insert(RelationFileClass rfc) {
        try {
            relationFileClassMapper.insert(rfc);
        } catch (Exception ignored) {

        }
    }

    public static void insert(RelationClassMethod rcf) {
        try {
            relationClassMethodMapper.insert(rcf);
        } catch (Exception ignored) {

        }
    }

    public static void insert(MavenRepository mr) {
        try {
            mavenRepositoryMapper.insert(mr);
        } catch (Exception ignored) {

        }
    }

    public static void close() {
        session.close();
    }

}
