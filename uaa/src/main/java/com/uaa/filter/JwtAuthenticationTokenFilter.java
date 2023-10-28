package com.uaa.filter;

import com.uaa.entity.User;
import com.uaa.service.UserService;
import com.uaa.utils.EncryptionUtil;
import com.uaa.vo.LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * token过滤器 验证token有效性
 *
 * 检验用户token的过滤器。对于客户端发出的请求，首先对用户的token进行校验，如果token不合法表示当前用户未登录，
 * 继续执行其他过滤器的逻辑；如果token合法则设置SecurityContextHolder表示用户已被认证。
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException,
            IOException {
        String token = request.getHeader(EncryptionUtil.TOKEN_HEADER);
        if (StringUtils.isEmpty(token) || !token.startsWith(EncryptionUtil.TOKEN_PREFIX)){
            chain.doFilter(request,response);
            return;
        }
        try {
            //如果能获取到token则Authentication进行设置，表示已认证
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //继续执行其他过滤器的逻辑
        chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) throws Exception {
        String token = tokenHeader.replace(EncryptionUtil.TOKEN_PREFIX,"");
        log.debug("token:"+token);
        //判断token是否过期
        boolean expiration = EncryptionUtil.isExpiration(token);
        if (expiration){
            log.error("token过期了");
            return null;
        }else{
            String username = EncryptionUtil.getUsername(token);
            User user = userService.getByName(username);

            List<String> permissions = userService.getPermissionById(user.getId());
            LoginUserVO loginUser = new LoginUserVO(user, Collections.singleton(new SimpleGrantedAuthority(user.getRole())));
            loginUser.setPermissions(new HashSet<>(permissions));
            //新建一个UsernamePasswordAuthenticationToken用来设置Authentication
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            return authenticationToken;
        }
    }
}