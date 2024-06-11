package com.lianxi.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyTokenRequestFilter extends BasicAuthenticationFilter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenService tokenService;

    public MyTokenRequestFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            //从请求头中获取token
            String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String key = requestTokenHeader.substring(7);

                Token token = null;
                try {
                    token = tokenService.verifyToken(key);
                } catch (Exception e) {
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("token 不合法");
                    return;
                }
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(token.getExtendedInformation());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                chain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            //登录发生异常,但要继续走其余过滤器的逻辑
            e.printStackTrace();
        }
        //继续执行springsecurity的过滤器
        chain.doFilter(request, response);
    }

}


