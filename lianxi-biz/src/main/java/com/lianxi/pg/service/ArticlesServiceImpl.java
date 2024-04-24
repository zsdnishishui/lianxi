package com.lianxi.pg.service;


import com.lianxi.pg.entity.Articles;
import com.lianxi.pg.mapper.ArticlesMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticlesServiceImpl implements ArticlesService {

    @Autowired
    private ArticlesMapper studentMapper;

    @Override
    public Articles getById(Integer id) {
        return studentMapper.getById(id);
    }

    @Override
    @GlobalTransactional
    public int add(Articles articles) {
        return studentMapper.add(articles);
    }

    @Override
    public int update(Articles articles) {
        return studentMapper.update(articles);
    }

    @Override
    public int delete(Integer id) {
        return studentMapper.delete(id);
    }
}
