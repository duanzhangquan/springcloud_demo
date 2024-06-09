package com.base.service.security;

import com.base.entity.SysUser;
import com.base.service.PermissionService;
import com.base.service.UserService;
import com.base.vo.SecurityLoginUserVO;
import com.pojo.vo.UserRolePermissionInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;



    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        SysUser user = userService.getByAccount(account);
        if(user == null){
            log.error("用户不存在:", account);
            throw new UsernameNotFoundException("用户不存在,"+account);
        }

        //查询角色权限信息
        List<UserRolePermissionInfoVO> list = permissionService.listById(user.getId());
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        list.stream().forEach(e->{
            if(e.getPermissionList() != null && e.getPermissionList().size() >0){
                e.getPermissionList().stream().forEach(t->{
                    //设置权限
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(t.getUrl());
                    //多个不同的角色可能被分配相同的权限,所以需要使用set去重
                    authorities.add(authority);
                });
            }
        });

        return new SecurityLoginUserVO(user,authorities);
    }
}
