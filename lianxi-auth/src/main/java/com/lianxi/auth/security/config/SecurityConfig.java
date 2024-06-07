package com.lianxi.auth.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//标注这是一个配置类
@Configuration
//开启SpringSecurity ;debug:是否开启Debug模式
@EnableWebSecurity(debug = false)
public class SecurityConfig {
    /**
     * 密码器
     * 密码加密功能
     * 创建实现类BCryptPasswordEncoder注入容器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //自定义登录页面,
        httpSecurity.formLogin()
                //设置请求登录认证页面
                .loginPage("/login.html")
                //设置请求登录的url
                .loginProcessingUrl("/user/login")
                //登录认证成功之后跳转路径,permitAll表示无条件进行访问
                .defaultSuccessUrl("/success").permitAll()
                .and().authorizeRequests()
                //设置哪些请求路径不需要认证可以直接访问
                .mvcMatchers("/index","/login.html","/hello").permitAll()
                //表示所有请求方式都可以访问
                .anyRequest().authenticated()
                //通过csrf的防护方式:disable关闭
                .and().csrf().disable();
        return httpSecurity.build();
    }


}

