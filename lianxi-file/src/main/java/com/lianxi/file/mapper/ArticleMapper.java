package com.lianxi.file.mapper;

import com.lianxi.file.enity.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper {
    List<Article> getAllArticle();
}
