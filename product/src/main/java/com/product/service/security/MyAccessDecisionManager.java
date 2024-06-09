package com.product.service.security;

import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.common.util.enumeration.SpringSecurityPermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 权限决策管理器，判断用户是否有访问权限
 */
@Service
@Slf4j
public class MyAccessDecisionManager implements AccessDecisionManager {

    /**
     * 判定是否拥有权限的决策
     *  authentication是userDetailService中循环添加到 GrantedAuthority对象中的权限信息集合.
     *  object包含客户端发起的请求的HttpServletRequest;
     *  configAttributes为MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回的结果，
     *   此方法是为了判定用户请求的url是否在权限表中，如果在权限表中，则返回给 decide方法，用来判定用户是否有此权限。如果不在权限表中则放行
     *
     *   注意：当configAttributes参数为空时springSecurity不会回调这个方法
     * @param authentication
     * @param object
     * @param configAttributes
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        log.debug("开始权限决策");

        List<ConfigAttribute> list = (List)configAttributes;
        String attributeName = SpringSecurityPermissionEnum.ACCESS_DENIED.getVal();
        if(list.get(0).getAttribute().equals(attributeName)){
            log.error("权限不足,拒绝访问");
            throw new AccessDeniedException(HttpServletResponseCodeEnum.ACCESS_DENIED.getMsg());
        }

        for(Iterator<ConfigAttribute> iter = configAttributes.iterator(); iter.hasNext(); ) {
            String auth =  iter.next().getAttribute();
            //未配置任何权限，允许访问
            attributeName = SpringSecurityPermissionEnum.ACCESS_ALLOW.getVal();
            if(attributeName.equals(auth) && authentication instanceof UsernamePasswordAuthenticationToken){
                return;
            }
            //遍历当前用户的权限列表,权限匹配时放行
            for(GrantedAuthority e : authentication.getAuthorities()) {
                if(auth.trim().equals(e.getAuthority())) {
                    log.debug("匹配权限");
                    return;
                }
            }
        }

        log.error("权限不足,拒绝访问");
        throw new AccessDeniedException(HttpServletResponseCodeEnum.ACCESS_DENIED.getMsg());
    }



    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
