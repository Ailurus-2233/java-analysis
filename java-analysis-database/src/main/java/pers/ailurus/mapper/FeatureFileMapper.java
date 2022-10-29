package pers.ailurus.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pers.ailurus.model.FeatureFile;

public interface FeatureFileMapper {
    int deleteByPrimaryKey(String md5);

    int insert(FeatureFile record);

    int insertOrUpdate(FeatureFile record);

    int insertOrUpdateSelective(FeatureFile record);

    int insertSelective(FeatureFile record);

    FeatureFile selectByPrimaryKey(String md5);

    List<FeatureFile> selectByNumberFeature(FeatureFile record);

    List<FeatureFile> selectByNumberFeatureFuzzy(FeatureFile record);

    int updateByPrimaryKeySelective(FeatureFile record);

    int updateByPrimaryKey(FeatureFile record);

    int updateBatch(List<FeatureFile> list);

    int updateBatchSelective(List<FeatureFile> list);

    int batchInsert(@Param("list") List<FeatureFile> list);
}