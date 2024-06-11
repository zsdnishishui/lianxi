package com.lianxi.auth.security.config;

import com.lianxi.auth.security.MyAuthenticationEntryPoint;
import com.lianxi.auth.security.MyTokenRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;
import java.util.Arrays;

//标注这是一个配置类
@Configuration
//开启SpringSecurity ;debug:是否开启Debug模式
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSucessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Resource
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;

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
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 设置加载用户信息的类
        provider.setUserDetailsService(userDetailsService);
        // 比较用户密码的时候，密码加密方式
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(Arrays.asList(provider));
    }


    //配置自定义的对token进行验证的过滤器
    @Bean
    public MyTokenRequestFilter myTokenRequestFilter() {
        return new MyTokenRequestFilter(authenticationManager(userDetailsService, passwordEncoder()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //自定义登录页面,
        httpSecurity.formLogin()
                //设置请求登录认证页面
//                .loginPage("/login.html")
                //设置请求登录的url
                .loginProcessingUrl("/user/login")
                //登录认证成功之后跳转路径,permitAll表示无条件进行访问
//                .defaultSuccessUrl("/success")
                .successHandler(authenticationSucessHandler)
                .failureHandler(authenticationFailureHandler) // 处理登录失败
                .permitAll()
                .and().authorizeRequests()
                //设置哪些请求路径不需要认证可以直接访问
                .mvcMatchers("/index", "/hello").permitAll()
                //表示所有请求方式都可以访问
                .anyRequest().authenticated()
                //异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)

                // 自定义过滤器
                .and()
                .addFilter(myTokenRequestFilter())
                //通过csrf的防护方式:disable关闭
                .csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);    //禁用session;
        return httpSecurity.build();
    }


}

