package pers.ailurus.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pers.ailurus.model.FeatureMethod;

@Mapper
public interface FeatureMethodMapper {
    @Insert("insert into feature_method (md5, modifier, args_num, return_type, cfg_finger) values (#{md5}, #{modifier}, #{argsNum}, #{returnType}, #{cfgFinger})")
    void insert(FeatureMethod fm);
}
