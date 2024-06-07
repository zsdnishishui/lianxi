package com.lianxi.auth.security;

import com.lianxi.auth.enity.User;
import com.lianxi.auth.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户信息
        User user = userMapper.getByUsername(username);
        //如果没有查询到用户就抛出异常
        if (Objects.isNull(user)) {
            log.error("Query returned no results for user '" + username + "'");
            throw new UsernameNotFoundException("查无该用户,请重试:\t" + username);
        } else {
            //TODO 查询对应的权限信息
            //设置权限集合,后续需要数据库查询(授权篇讲解,这里定义死)
            List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList("role");
            // 3. 返回UserDetails类型用户
            return new TestUserDetails(username, user.getPassword(), user.getPhone(), authorityList,
                    true, true, true, true); // 账号状态这里都直接设置为启用，实际业务可以存在数据库中

        }
    }
}

