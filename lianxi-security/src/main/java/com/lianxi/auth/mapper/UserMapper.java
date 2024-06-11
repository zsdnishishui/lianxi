package com.lianxi.auth.mapper;

import com.lianxi.auth.enity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from t_user where user_name = #{username}")
    User getByUsername(String username);

    @Insert("insert into t_user(user_name,password) values(#{username},#{password})")
    int addUser(User user);
}
