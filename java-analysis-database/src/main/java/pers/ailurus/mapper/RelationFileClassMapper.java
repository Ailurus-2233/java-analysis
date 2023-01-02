package pers.ailurus.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pers.ailurus.model.RelationFileClass;

import java.util.List;

@Mapper
public interface RelationFileClassMapper {
    @Insert("insert into relation_file_class (file_md5, class_md5) values (#{fileMd5}, #{classMd5})")
    void insert(RelationFileClass rfc);

    @Select("select file_md5 from relation_file_class where class_md5 = #{classMd5}")
    List<String> selectFileMd5ByClassMd5(String key);

    @Select("select class_md5 from relation_file_class where file_md5 = #{fileMd5}")
    List<String> selectClassMd5ByFileMd5(String md5);
}
