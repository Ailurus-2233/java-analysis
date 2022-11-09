package pers.ailurus.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pers.ailurus.model.MavenRepository;

public interface MavenRepositoryMapper {
    int deleteByPrimaryKey(String md5);

    int insert(MavenRepository record);

    int insertOrUpdate(MavenRepository record);

    int insertOrUpdateSelective(MavenRepository record);

    int insertSelective(MavenRepository record);

    MavenRepository selectByPrimaryKey(String md5);

    int updateByPrimaryKeySelective(MavenRepository record);

    int updateByPrimaryKey(MavenRepository record);

    int updateBatch(List<MavenRepository> list);

    int batchInsert(@Param("list") List<MavenRepository> list);

    int updateBatchSelective(List<MavenRepository> list);

    List<MavenRepository> selectMavenDownloadList(@Param("count") int count);

    List<MavenRepository> selectMavenListWithZeroClass();
}