package pers.ailurus.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pers.ailurus.model.RelationClassMethod;

import java.util.List;

@Mapper
public interface RelationClassMethodMapper {
    @Insert("insert into relation_class_method (class_md5, method_md5) values (#{classMd5}, #{methodMd5})")
    void insert(RelationClassMethod rcm);

    @Select("select class_md5 from relation_class_method where method_md5 = #{md5}")
    List<String> selectClassMd5ByMethodMd5(String md5);

    @Select("select method_md5 from relation_class_method where class_md5 = #{md5}")
    List<String> selectMethodMd5ByClassMd5(String md5);
}
