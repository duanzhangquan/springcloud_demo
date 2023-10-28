package com.uaa.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uaa.entity.User;
import com.uaa.mapper.UserMapper;
import com.uaa.vo.LoginUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_name",username);
        User user = (User) userMapper.selectList(wrapper).get(0);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        return new LoginUserVO(user,authorities);
    }
}
