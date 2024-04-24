package com.lianxi.pg.mapper;

import com.lianxi.pg.entity.Articles;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ArticlesMapper {

    @Select("select * from articles where id = #{id}")
    Articles getById(Integer id);

    @Insert("insert into articles(id,title) values(#{id},#{title})")
    int add(Articles articles);

    @Update("update articles set title = #{title} where id = #{id}")
    int update(Articles articles);

    @Delete("delete from articles where id = #{id}")
    int delete(Integer id);
}
