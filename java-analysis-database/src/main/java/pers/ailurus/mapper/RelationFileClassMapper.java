package pers.ailurus.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pers.ailurus.model.RelationFileClass;

public interface RelationFileClassMapper {
    int deleteByPrimaryKey(@Param("fileMd5") String fileMd5, @Param("classMd5") String classMd5);

    int insert(RelationFileClass record);

    int insertOrUpdate(RelationFileClass record);

    int insertOrUpdateSelective(RelationFileClass record);

    int insertSelective(RelationFileClass record);

    int updateBatch(List<RelationFileClass> list);

    int updateBatchSelective(List<RelationFileClass> list);

    int batchInsert(@Param("list") List<RelationFileClass> list);

    List<String> selectFileMd5ByClassMd5(@Param("classMd5") String classMd5);

    List<String> selectClassMd5ByFileMd5(@Param("fileMd5") String md5);
}