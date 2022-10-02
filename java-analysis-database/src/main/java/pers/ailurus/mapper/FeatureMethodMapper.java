package pers.ailurus.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pers.ailurus.model.FeatureMethod;

public interface FeatureMethodMapper {
    int deleteByPrimaryKey(String md5);

    int insert(FeatureMethod record);

    int insertOrUpdate(FeatureMethod record);

    int insertOrUpdateSelective(FeatureMethod record);

    int insertSelective(FeatureMethod record);

    FeatureMethod selectByPrimaryKey(String md5);

    int updateByPrimaryKeySelective(FeatureMethod record);

    int updateByPrimaryKey(FeatureMethod record);

    int updateBatch(List<FeatureMethod> list);

    int updateBatchSelective(List<FeatureMethod> list);

    int batchInsert(@Param("list") List<FeatureMethod> list);
}