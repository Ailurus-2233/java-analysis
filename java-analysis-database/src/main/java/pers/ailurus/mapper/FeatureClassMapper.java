package pers.ailurus.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pers.ailurus.model.FeatureClass;

import java.util.List;
import java.util.Map;

@Mapper
public interface FeatureClassMapper {
    @Insert("insert into " +
            "feature_class (md5, modifier, interface_num, has_super_class, field_num, method_num, dep_class_num, be_dep_num), " +
            "values (#{md5}, #{modifier}, #{interfaceNum}, #{hasSuperClass}, #{fieldNum}, #{methodNum}, #{depClassNum}, #{beDepNum})")
    void insert(FeatureClass featureClass);

    @Select("select * " +
            "from feature_class " +
            "where modifier = #{modifier} and interface_num = #{interfaceNum} and has_super_class = #{hasSuperClass} and field_num = #{fieldNum} and method_num = #{methodNum} and dep_class_num = #{depClassNum} and be_dep_num = #{beDepNum}")
    List<FeatureClass> selectByNumberFeature(FeatureClass featureClass);

    @Select("select md5 " +
            "from feature_class left join relation_file_class on class_md5 = md5 " +
            "where " +
            "modifier = #{modifier} " +
            "and interface_num = #{interfaceNum} " +
            "and has_super_class = #{hasSuperClass} " +
            "and field_num = #{fieldNum} " +
            "and method_num = #{methodNum} " +
            "and dep_class_num = #{depClassNum} " +
            "and be_dep_num = #{beDepNum} " +
            "and file_md5 in ${ffMd5}")
    List<String> selectByNumberFeatureWithFileMd5(Map map);

    @Select("select * from feature_class where md5 = #{md5} limit 1")
    FeatureClass selectByPrimaryKey(String md5);
}
