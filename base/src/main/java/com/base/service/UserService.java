package com.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.mapper.UserMapper;
import com.base.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author duan
 */
@Service
public class UserService extends ServiceImpl<UserMapper,SysUser> {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取一个用户
     * @param account
     * @return
     */
    public SysUser getByAccount(String account){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("account",account);
        SysUser user = userMapper.selectOne(wrapper);

        return user;
    }

    public void addUser(SysUser user){
        super.save(user);
    }

    public void deleteUser(Long userId){
        super.removeById(userId);
    }

    public void updateUser(SysUser user){
        super.updateById(user);
    }

}
