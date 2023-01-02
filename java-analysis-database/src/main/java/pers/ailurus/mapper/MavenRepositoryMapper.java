package pers.ailurus.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pers.ailurus.model.MavenRepository;

import java.util.List;

@Mapper
public interface MavenRepositoryMapper {
    @Insert("insert into maven_repository (group_id, artifact_id, version, url, size) values (#{groupId}, #{artifactId}, #{version}, #{url}, #{size})")
    void insert(MavenRepository mr);

    @Select("select * from maven_repository limit 0, #{count}")
    List<MavenRepository> selectMavenDownloadList(int count);

}
