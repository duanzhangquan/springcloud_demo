/*
package com.product.service.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

*/
/**
 * 自定义角色投票器
 *//*

@Component
@Slf4j
public class MyAccessDecisionVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public int vote(Authentication authentication, Object o, Collection<ConfigAttribute> attributes) {
        log.debug("开始投票");
        if (authentication == null) {
            //拒绝
            return ACCESS_DENIED;
        }

        int result = ACCESS_ABSTAIN;
        //获取当前登录用户的权限
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (CollectionUtils.isEmpty(a.getAuthorities())) {
            log.debug("没有设置任何权限,直接弃权");
            return ACCESS_ABSTAIN;
        }

        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) a.getAuthorities();
        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                //拒绝
                result = ACCESS_DENIED;

                // 尝试查找所有匹配的权限
                for (GrantedAuthority authority : authorities) {
                    if (attribute.getAttribute().equals(authority.getAuthority())) {
                        //授权
                        result = ACCESS_GRANTED;
                        break;
                    }
                }

                if (result == ACCESS_DENIED) {
                    return ACCESS_DENIED;
                }
            }
        }

        return  result;
    }

    @Override
    public boolean supports(Class aClass) {
        return false;
    }
}
*/
