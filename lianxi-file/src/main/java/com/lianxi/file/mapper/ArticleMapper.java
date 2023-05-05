package com.lianxi.file.mapper;

import com.lianxi.file.enity.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper {
    List<Article> getAllArticle(String keyword);

    Article getById(Integer id);

    int updateTokens(Map<String, Object> res);
}
