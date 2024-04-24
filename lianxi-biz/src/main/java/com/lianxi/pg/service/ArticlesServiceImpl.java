package com.lianxi.pg.service;


import com.lianxi.core.domain.R;
import com.lianxi.pg.entity.Articles;
import com.lianxi.pg.feignClient.RemoteServiceClient;
import com.lianxi.pg.mapper.ArticlesMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticlesServiceImpl implements ArticlesService {

    @Autowired
    private ArticlesMapper studentMapper;

    @Autowired
    private RemoteServiceClient remoteServiceClient;

    @Override
    public Articles getById(Integer id) {
        return studentMapper.getById(id);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public int add(Articles articles) {
        R<Integer> r = remoteServiceClient.delete(11);
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
