package com.lianxi.auth.security.config;

import com.lianxi.auth.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

//标注这是一个配置类
@Configuration
//开启SpringSecurity ;debug:是否开启Debug模式
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    //白名单
    private static final String[] URL_WHITELIST = {
            "/hello"
    };

    @Bean
    public AuthenticationSuccessHandler authenticationSucessHandler() {
        return new MyAuthenticationSucessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new MyAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationEntryPoint myAuthenticationEntryPoint() {
        return new MyAuthenticationEntryPoint();
    }

    @Bean
    public MyAccessDeniedHandler myAccessDeniedHandler() {
        return new MyAccessDeniedHandler();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl();
    }

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


    //配置自定义的对token进行验证的过滤器
    @Bean
    public AuthTokenRequestFilter myTokenRequestFilter() {
        return new AuthTokenRequestFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //自定义登录页面,
        httpSecurity.cors().and().csrf().disable()
                .formLogin()
                //设置请求登录的url
                .loginProcessingUrl("/user/login")
                .successHandler(authenticationSucessHandler()) // 处理登录成功
                .failureHandler(authenticationFailureHandler()) // 处理登录失败
                //配置拦截器
                //anonymous() 允许匿名用户访问,不允许已登入用户访问
                //permitAll() 不管登入,不登入 都能访问
                .and().authorizeRequests()
                //设置哪些请求路径不需要认证可以直接访问
                .antMatchers(URL_WHITELIST).permitAll()
                //表示所有请求方式都可以访问
                .anyRequest().authenticated()
                //异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint())
                .accessDeniedHandler(myAccessDeniedHandler())
                // 自定义过滤器
                .and()
                .addFilterAfter(myTokenRequestFilter(), BasicAuthenticationFilter.class)
                //通过csrf的防护方式:disable关闭
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);    //禁用session;

        return httpSecurity.build();
    }


}

