package pers.ailurus.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pers.ailurus.model.FeatureClass;

public interface FeatureClassMapper {
    int deleteByPrimaryKey(String md5);

    int insert(FeatureClass record);

    int insertOrUpdate(FeatureClass record);

    int insertOrUpdateSelective(FeatureClass record);

    int insertSelective(FeatureClass record);

    FeatureClass selectByPrimaryKey(String md5);

    int updateByPrimaryKeySelective(FeatureClass record);

    int updateByPrimaryKey(FeatureClass record);

    int updateBatch(List<FeatureClass> list);

    int updateBatchSelective(List<FeatureClass> list);

    int batchInsert(@Param("list") List<FeatureClass> list);
}