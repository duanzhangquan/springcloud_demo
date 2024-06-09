package com.ldap.filter;

import com.alibaba.fastjson.JSON;
import com.base.vo.SecurityLdapLoginUserVO;
import com.base.vo.SecurityLoginUserVO;
import com.common.exception.BizException;
import com.common.util.EncryptionUtil;
import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.HttpServletResponseResultWriter;
import com.common.util.enumeration.AccountTypeEnum;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.dto.LdapUserDTO;
import com.pojo.dto.UserDTO;
import com.pojo.vo.UserTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 用户名密码登录过滤器,登录时对用户名密码进行校验，校验成功后返回一个token给客户端，
 * 下次客户端访问业务接口时在请求头带上此token代表该用户已经被认证。
 *
 * 注意：UsernamePasswordAuthenticationFilter这个过滤器默认只能接收表单请求参数，如果要接收json参数
 * 则必须重写此过滤器
 */

@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * AbstractSecurityInterceptor a;
     * 访问url时通过AbstractSecurityInterceptor拦截器拦截
     * 然后调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限，在调用授权管理器AccessDecisionManager，
     * 这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，还会获取被拦截的url和被拦截url所需的全部权限，
     * 然后根据所配的策略（有：一票决定，一票否定，少数服从多数等），如果权限足够，则返回，权限不够则报错(抛出异常)。
     */

    private ThreadLocal<Integer> rememberMe = new ThreadLocal<>();
    private AuthenticationManager authenticationManager;

    public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        //设置springsecurity的登录接口地址
        super.setFilterProcessesUrl("/login");
    }


    /**
     *
     接收 /ldap/login接口提交的登录数据并进行认证
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LdapUserDTO dto = null;
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        try {
            dto = new ObjectMapper().readValue(request.getInputStream(), LdapUserDTO.class);
        } catch (IOException e) {
            throw new BizException(HttpServletResponseCodeEnum.NOT_AVAILABILITY);
        }
        rememberMe.set(dto.getRememberMe() == null ? 0 : dto.getRememberMe());
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getAccount(), dto.getPassword(), new ArrayList<>())
        );
    }

    /**
     * 用户身份验证成功后由springsecurity回调,
     * 如果验证成功，就生成token并返回
     * @param request
     * @param response
     * @param chain
     * @param authResult
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {

        SecurityLdapLoginUserVO userVO = (SecurityLdapLoginUserVO) authResult.getPrincipal();
        log.info("LoginUserVO:",userVO);
        String token = EncryptionUtil.createToken(userVO.getUserId(), AccountTypeEnum.LDAP_USER.getValue());
        String refreshToken = EncryptionUtil.createRefreshToken(userVO.getUserId());
        UserTokenVO u = new UserTokenVO(EncryptionUtil.TOKEN_PREFIX+ token,
                EncryptionUtil.TOKEN_PREFIX +refreshToken, userVO.getUserId());
        u.setAccountType(AccountTypeEnum.LDAP_USER.getValue());
        // 返回创建成功的token
        int code = HttpServletResponseCodeEnum.OK.getCode();
        String message = HttpServletResponseCodeEnum.OK.getMsg();
        HttpServletResponseResultWrappers<UserTokenVO> r = new HttpServletResponseResultWrappers(u,code,message);
        try {
            HttpServletResponseResultWriter.writeOk(response,r);
        } catch (IOException e) {
            log.error("springSecurity登录回调异常",e);
            throw new BizException(HttpServletResponseCodeEnum.AUTHENTICATION_FAIL.getMsg(),e);
        }
    }

    /**
     * 用户身份验证失败后由springsecurity回调
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        int code = HttpServletResponseCodeEnum.AUTHENTICATION_FAIL.getCode();
        String msg = HttpServletResponseCodeEnum.AUTHENTICATION_FAIL.getMsg();
        String cause = ",原因:" +failed.getMessage();
        HttpServletResponseResultWrappers resultWrappers = new HttpServletResponseResultWrappers("",code,msg+cause);
        response.getWriter().write(JSON.toJSONString(resultWrappers));
    }
}
