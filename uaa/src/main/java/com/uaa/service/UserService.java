package com.uaa.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uaa.entity.User;
import com.uaa.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author duan
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getByName(String username){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_name",username);
        User user = (User) userMapper.selectList(wrapper).get(0);

        return user;
    }

    public List<String> getPermissionById(long id){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("id",id);
        User user = userMapper.selectById(id);
        String permission = user.getPermission();
        if(StringUtils.isEmpty(permission)){
            return new ArrayList<>();
        }

        return Arrays.asList(permission.split(","));
    }

}
