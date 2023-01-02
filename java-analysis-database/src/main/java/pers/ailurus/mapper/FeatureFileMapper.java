package pers.ailurus.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pers.ailurus.model.FeatureFile;

import java.util.List;

@Mapper
public interface FeatureFileMapper {
    @Insert("insert into feature_file (md5, group_id, artifact_id, version, class_num, package_deep, package_num) values (#{md5}, #{groupId}, #{artifactId}, #{version}, #{classNum}, #{packageDeep}, #{packageNum});")
    void insert(FeatureFile ff);

    @Select("select * from feature_file where class_num = #{classNum} and package_deep = #{packageDeep} and package_num = #{packageNum};")
    List<FeatureFile> selectByNumberFeature(FeatureFile featureFile);

    @Select("select * from feature_file where md5 = #{md5};")
    FeatureFile selectByPrimaryKey(String md5);

    @Select("select md5 from feature_file where class_num = #{classNum} and package_deep = #{packageDeep} and package_num = #{packageNum} group by md5;")
    List<String> selectMd5ByNumberFeature(FeatureFile featureFile);
}
