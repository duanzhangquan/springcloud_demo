package com.uaa.filter;

import com.base.service.CustomAuthenticationService;
import com.common.util.EncryptionUtil;
import com.common.util.enumeration.AccountTypeEnum;
import com.pojo.vo.UserTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * jwt token校验过滤器,
 * 对不支持匿名访问的接口进行统一拦截,如果未携带token或token不合法或已过期，则身份认证失败，否则对要
 * 访问的业务接口放行
 */
@Slf4j
@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private UserDetailsService userDetailsService;
    @Autowired
    private CustomAuthenticationService customAuthenticationService;

    public void setUserDetailsService(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
        log.debug("userDetailsService:"+userDetailsService);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("校验用户token");
        String url = request.getRequestURI();
        if(url.lastIndexOf("/uaa/login") >-1){
            log.debug("登录接口不做拦截");
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(EncryptionUtil.USER_TOKEN_HEADER);
        String refreshToken = request.getHeader(EncryptionUtil.USER_REFRESH_TOKEN_HEADER);
        String userAccount = request.getHeader(EncryptionUtil.USER_ACCOUNT_HEADER);
        UserTokenVO userTokenVO = new UserTokenVO(token,refreshToken,userAccount, AccountTypeEnum.NORMAL_USER.getValue());
        boolean validated = customAuthenticationService.validateUser(userTokenVO,response);
        if(!validated){
            log.debug("校验用户未通过:",userTokenVO);
            return;
        }
        customAuthenticationService.setAuthentication(userAccount,userDetailsService);

        //继续执行过滤器链上的其它过滤器
        filterChain.doFilter(request, response);

    }
}
