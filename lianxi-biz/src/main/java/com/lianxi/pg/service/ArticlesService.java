package com.lianxi.pg.service;


import com.lianxi.pg.entity.Articles;


public interface ArticlesService {


    Articles getById(Integer id);

    int add(Articles articles);

    int update(Articles articles);

    int delete(Integer id);
}
