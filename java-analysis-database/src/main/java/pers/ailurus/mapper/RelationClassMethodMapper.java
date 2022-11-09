package pers.ailurus.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pers.ailurus.model.RelationClassMethod;

public interface RelationClassMethodMapper {
    List<String> selectClassMd5ByMethodMd5(String methodMd5);

    List<String> selectMethodMd5ByClassMd5(String md5);

    int deleteByPrimaryKey(@Param("classMd5") String classMd5, @Param("methodMd5") String methodMd5);

    int insert(RelationClassMethod record);

    int insertOrUpdate(RelationClassMethod record);

    int insertOrUpdateSelective(RelationClassMethod record);

    int insertSelective(RelationClassMethod record);

    int updateBatch(List<RelationClassMethod> list);

    int updateBatchSelective(List<RelationClassMethod> list);

    int batchInsert(@Param("list") List<RelationClassMethod> list);

}