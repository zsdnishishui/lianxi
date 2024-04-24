package com.lianxi.pg.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceProxyConfig {

    @Autowired
    private DataSourceConfig dataSourceConfig;


    @Bean("dataSource")
    public DataSource druidDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(dataSourceConfig.getUsername());
        hikariDataSource.setPassword(dataSourceConfig.getPassword());
        hikariDataSource.setJdbcUrl(dataSourceConfig.getUrl());
        hikariDataSource.setDriverClassName(dataSourceConfig.getDriveClassName());
        return hikariDataSource;
    }

    @Bean
    @Primary
    public DataSourceProxy dataSourceProxy(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

}
