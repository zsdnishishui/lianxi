package com.lianxi.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 自定义登录成功逻辑
 * @Date: 2020-2-9
 */
@Service
public class MyAuthenticationSucessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录成功
     *
     * @param request：请求
     * @param response：响应
     * @param authentication：Authentication参数既包含了认证请求的一些信息，比如IP，请求的SessionId等， 也包含了用户信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>();
        map.put("token", tokenService.allocateToken(request.getParameter("username")).getKey());
        map.put("authentication", authentication);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(mapper.writeValueAsString(map));
    }
}