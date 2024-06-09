package com.product.service.security;
import com.common.util.enumeration.SpringSecurityPermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 动态权限数据源
 * @author duanzhangquan
 */
@Slf4j
@Service
public class MyInvocationSecurityMetadataSourceService  implements FilterInvocationSecurityMetadataSource {

    /**
     * 此方法是判定当前用户请求的url是否包含在用户权限集合中,如果包含则返回给
     * MyAccessDecisionManager类的decide方法，如果用户未配置任何权限则放行。
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        String contextPath = request.getContextPath();
        // 获取当前请求的URL
        String requestUrl = contextPath + ((FilterInvocation) object).getRequestUrl();
        log.debug("requestUrl:"+requestUrl);
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if(CollectionUtils.isEmpty(a.getAuthorities())){
            //如果用户未配置任何权限,则可以任意访问
            log.debug("用户"+a.getPrincipal()+"未配置任何权限,放行");
            String attributeName = SpringSecurityPermissionEnum.ACCESS_ALLOW.getVal();
            return SecurityConfig.createList(attributeName);
        }
        //得到当前登录用户的所有权限
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) a.getAuthorities();

        Collection<ConfigAttribute> configAttributes = new ArrayList<>();
        //取当前用户请求的url去和用户的权限集合依次进行匹配
        Iterator<GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()){
            String auth = iterator.next().getAuthority();
            SecurityConfig config = new SecurityConfig(auth);
            configAttributes.add(config);
            if(auth.lastIndexOf(requestUrl) >= 0){
                log.debug(requestUrl+"匹配当前用户权限");
                //返回匹配后的url权限
                return configAttributes;
            }
            else{
                log.debug(requestUrl+"不匹配当前用户权限");
                //当前用户请求的url和权限集合的下一个权限进行匹配
                continue;
            }
        }

        //拒绝访问
        String attributeName = SpringSecurityPermissionEnum.ACCESS_DENIED.getVal();
        return SecurityConfig.createList(attributeName);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
