package com.uaa.service;

import com.alibaba.nacos.api.utils.StringUtils;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.base.util.SecurityUtil;
import com.base.vo.SecurityLoginUserVO;
import org.springframework.stereotype.Service;

/**
 * 自定义spring security权限
 * 1. 可以在用户自定义的方法上添加@PreAuthorize注解来配合使用
 * 2. 可以在org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
 * #configure(HttpSecurity)方法中通过.antMatchers("xxx").access("@auth.hasPer(xxx)")来使用
 */
@Service("auth")
public class MyPermissionService {

    /**
     * 判断某个用户是否对应的权限
     * @param permission
     * @return
     */
    public boolean hasPer(String permission) {
        if (StringUtils.isBlank(permission)){
            return false;
        }
        //获取当前登录的用户
        SecurityLoginUserVO loginUser = SecurityUtil.getLoginUser();
        if (loginUser == null || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        return loginUser.getPermissions().contains(StringUtils.trim(permission));
    }

    /**
     * 判断某个用户是否对应的角色
     * @param role
     * @return
     */
/*    public boolean hasRole(String role) {
        if (StringUtils.isBlank(role)){
            return false;
        }

        LoginUserVO loginUser = SecurityUtil.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return loginUser.getRole().equals(role);
    }*/
}
