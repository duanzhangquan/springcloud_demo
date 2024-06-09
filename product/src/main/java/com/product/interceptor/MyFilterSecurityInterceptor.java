package com.product.interceptor;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.base.entity.SysUser;
import com.base.entity.ldap.SysLdapUser;
import com.base.service.PermissionService;
import com.base.service.UserService;
import com.base.service.ldap.LdapPermissionService;
import com.base.vo.SecurityLdapLoginUserVO;
import com.base.vo.SecurityLoginUserVO;
import com.common.exception.BizException;
import com.common.exception.CustomTokenException;
import com.common.util.EncryptionUtil;
import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.HttpServletResponseResultWriter;
import com.common.util.JSONUtil;
import com.common.util.enumeration.AccountTypeEnum;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.feign.remote.LdapFeignClient;
import com.pojo.dto.AuthorityDTO;
import com.pojo.dto.GiteeOauth2UserIdentifierDTO;
import com.pojo.vo.UserRolePermissionInfoVO;
import com.pojo.vo.UserTokenVO;
import com.pojo.vo.ldap.LdapPersonVO;
import com.pojo.vo.ldap.LdapUserRolePermissionInfoVO;
import com.product.dto.UserAuthenticationDTO;
import com.product.service.security.MyAccessDecisionManager;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author duanzhangquan
 * url访问拦截器.
 *
 * 访问url时，会通过AbstractSecurityInterceptor拦截器拦截,
 * 然后会调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限。
 * 再调用授权管理器AccessDecisionManager,这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，
 * 还会获取被拦截的url和被拦截url所需的全部权限，然后根据所配的策略（有：一票决定，一票否定，少数服从多数等）进行决策。
 * 如果权限足够，则返回;权限不够则抛出异常提示权限不足。
 *
 * 需要解决问题：
 * 1. 该过滤器会在请求接口时执行两次!，可能是因为继承了Filter接口的原因，需要解决(改为继承OncePerRequestFilter接口试试)
 * 2. 如果不通过网关访问接口，访问不存在的接口时应该抛出404异常，而不是提示“权限不足,拒绝访问”
 */
@Service
@Slf4j
public class MyFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LdapFeignClient ldapFeignClient;
    @Autowired
    private LdapPermissionService ldapPermissionService;

    @Autowired
    public void setMyAccessDecisionManager(MyAccessDecisionManager myAccessDecisionManager) {
        super.setAccessDecisionManager(myAccessDecisionManager);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("初始化MyFilterSecurityInterceptor过滤器");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.debug("执行MyFilterSecurityInterceptor过滤器");
        boolean validated = validatePrincipal((HttpServletRequest)request,(HttpServletResponse)response,chain);
        if(validated){
            FilterInvocation f = new FilterInvocation(request, response, chain);
            invoke(f);
        }
    }


    @Override
    public void destroy() {

    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    /**
     * 获取权限数据源
     * @return
     */
    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }


    private void writeFailure(HttpServletResponse response,int code,String message,Object data) throws IOException {
        HttpServletResponseResultWrappers r = new HttpServletResponseResultWrappers(data,code,message);
        HttpServletResponseResultWriter.writeFailure(response,r);
    }

    /**
     * 校验用户身份
     *
     */
    private boolean validatePrincipal(HttpServletRequest req, HttpServletResponse rsp,FilterChain chain)
            throws IOException, ServletException {
        String token = req.getHeader(EncryptionUtil.USER_TOKEN_HEADER);
        String refreshToken = req.getHeader(EncryptionUtil.USER_REFRESH_TOKEN_HEADER);
        String userAccount = req.getHeader(EncryptionUtil.USER_ACCOUNT_HEADER);
        String accountType = req.getHeader(EncryptionUtil.USER_ACCOUNT_TYPE_HEADER);

        if(StringUtils.isEmpty(accountType)){
            int code =  HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getCode();
            String message = HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getMsg()+":accountType";
            log.error(message);
            HttpServletResponseResultWrappers r = new HttpServletResponseResultWrappers("",code,message);
            writeFailure(rsp,code,message,"");
            return false;
        }
        UserTokenVO u = new UserTokenVO(token,refreshToken,userAccount,Integer.parseInt(accountType));
        log.debug("UserTokenVO:"+u);

        if(StringUtils.isEmpty(userAccount)){
            int code =  HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getCode();
            String message = HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getMsg()+":account";
            log.error(message);
            writeFailure(rsp,code,message,"");
            return false;
        }
        if(!validateAccount(u)){
            int code =  HttpServletResponseCodeEnum.USER_NOT_EXIST.getCode();
            String message =
                    HttpServletResponseCodeEnum.USER_NOT_EXIST.getMsg()+",account:"+userAccount+",accountType:"+accountType;
            log.error(message);
            writeFailure(rsp,code,message,"");
            return false;
        }
        try{
            //校验token
            EncryptionUtil.validateToken(u);
        }
        catch (CustomTokenException e){
            int code = e.getBizResponseCodeEnum().getCode();
            String message = e.getBizResponseCodeEnum().getMsg();
            log.error(message,e);
            writeFailure(rsp,code,message,e.getData());
            return false;
        }

        //设置认证信息到springSecurity的安全上下文
        try{
            setAuthentication(token,Integer.parseInt(accountType),req);
        }catch (BizException e){
            log.error("调用setAuthentication方法产生异常",e);
            writeFailure(rsp,e.getCode(),e.getMessage(),"");
            return false;
        }

        return true;
    }

    /**
     * 校验账号
     * @return
     */
    private boolean validateAccount(UserTokenVO u){
        Integer accountType = u.getAccountType();
        String account = u.getAccount();

        //jar包调用userService.getByAccount接口
        if(accountType == AccountTypeEnum.NORMAL_USER.getValue()){
            SysUser user = userService.getByAccount(account);
            if(user == null){
                log.error("账号不存在,account:"+account+",accountType:"+accountType);
                return false;
            }
        }

        //openfeign远程调用http接口
        else if(accountType == AccountTypeEnum.LDAP_USER.getValue()){
            //传递请求头给openfeign
            String tokenHeader = u.getToken();
            String refreshTokenHeader = u.getRefreshToken();
            HttpServletResponseResultWrappers result = ldapFeignClient.
                    getPerson(account,tokenHeader,refreshTokenHeader,account);
            int httpResultCode = result.getCode();
            if(httpResultCode != HttpServletResponseCodeEnum.OK.getCode()){
                if(httpResultCode == HttpServletResponseCodeEnum.TOKEN_NEED_REFRESH.getCode()){
                    log.error("请刷新token");
                    return false;
                }
                else if(httpResultCode == HttpServletResponseCodeEnum.TOKEN_EXPIRED.getCode()){
                    log.error("token已过期,请重新登录");
                    return false;
                }
                else if(httpResultCode == HttpServletResponseCodeEnum.USER_NOT_EXIST.getCode()){
                    log.error("账号不存在,account:"+account+",accountType:"+accountType);
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * 得到认证之后的身份数据并设置角色、权限数据到springSecurity上下文中
     * @param tokenHeader
     * @param request
     * @param accountType 1:普通用户 2:ldap用户
     *
     * 这个方法应该抽象成通用接口放到base模块中或抽象成通用的openfeign接口
     * @return
     */
    private void setAuthentication(String tokenHeader, Integer accountType,HttpServletRequest request) throws BizException{
        SysUser user = new SysUser();
        HashSet<SimpleGrantedAuthority> authorities = new HashSet<>();
        String userAccount = EncryptionUtil.parseToken(tokenHeader);
        log.debug("解析token得到userAccount:"+userAccount);
        //解析token得到account,如果account由userId、userName和registrationId的json串组成,
        // 则说明这个请求是由oauth2服务发起的
        if(JSONUtil.isJson(userAccount)){
            JSONObject obj = (JSONObject)JSONObject.parse(userAccount);
            if(obj.containsKey("userId") && obj.containsKey("userName") && obj.containsKey("registrationId")){
                oauth2UserAuthentication(authorities,userAccount,tokenHeader,user,request);
            }
        }
        //处理uaa、ldap和product服务发起的请求(包括openfeign调用和maven模块调用)
        else {
            UserAuthenticationDTO authentication = new UserAuthenticationDTO();
            authentication.setAccountType(accountType);
            authentication.setUserAccount(userAccount);
            authentication.setAuthorities(authorities);

            UserTokenVO u = new UserTokenVO();
            u.setToken(tokenHeader);
            u.setRefreshToken(request.getHeader(EncryptionUtil.USER_REFRESH_TOKEN_HEADER));
            try{
                userAuthentication(authentication,request,u);
            }catch (BizException e){
                log.error("调用userAuthentication方法产生异常",e);
                throw new BizException(e.getCode(),e.getMsg());
            }
        }
    }


    /**
     * oauth2.0用户认证
     *
     * 这个方法应该抽象成通用接口放到base模块中或抽象成通用的openfeign接口
     */
    private void oauth2UserAuthentication(HashSet<SimpleGrantedAuthority> authorities,
                                               String userAccount,String tokenHeader,SysUser user,
                                               HttpServletRequest request){
        GiteeOauth2UserIdentifierDTO u = JSONObject.parseObject(userAccount, GiteeOauth2UserIdentifierDTO.class);
        Claims c = EncryptionUtil.getTokenClaims(tokenHeader);
        String userName = u.getRegistrationId().concat("_"+u.getUserName());
        user.setId(u.getUserId().longValue());
        user.setUsername(userName);
        user.setAccount(userName);
        user.setPassword(u.getUserId().toString());
        user.setDefaultUser(0);

        //得到token中的用户权限
        List<LinkedHashMap> grantedAuthorities = (List<LinkedHashMap>)c.get("authorities");
        String str = JSONObject.toJSONString(grantedAuthorities);
        List<AuthorityDTO> authList = JSONObject.parseArray(str,AuthorityDTO.class);
        authList.stream().forEach(e->{
            authorities.add(new SimpleGrantedAuthority(e.getAuthority()));
        });

        SecurityLoginUserVO loginUser = new SecurityLoginUserVO(user,authorities);
        //新建一个UsernamePasswordAuthenticationToken用来设置Authentication
        //UsernamePasswordAuthenticationToken由用户名、密码和角色(或者权限)组成
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPassword(), authorities);
        //设置remoteAddr和sessionId
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * ldap用户和普通用户认证
     */
    private void userAuthentication(UserAuthenticationDTO authentication,
                                    HttpServletRequest request,UserTokenVO u) throws BizException{
        Integer accountType = authentication.getAccountType();
        String userAccount = authentication.getUserAccount();
        SysUser sysUser = null;
        HashSet<SimpleGrantedAuthority> authorities = authentication.getAuthorities();
        //springSecurity用户名
        Object principal = null;
        //springSecurity密码
        Object credentials = null;

        //普通账号,调用普通账号认证服务(通过jar包形式调用userService.getByAccount和permissionService.listById接口)
        if(AccountTypeEnum.NORMAL_USER.getValue() == accountType){
            sysUser = userService.getByAccount(userAccount);
            //查询用户角色和权限数据
            List<UserRolePermissionInfoVO> list = permissionService.listById(sysUser.getId());
            HashSet<SimpleGrantedAuthority> finalAuthorities = authorities;
            list.stream().forEach(e->{
                if(e.getPermissionList() != null && e.getPermissionList().size() >0){
                    e.getPermissionList().stream().forEach(t->{
                        //设置权限
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(t.getUrl());
                        finalAuthorities.add(authority);
                    });
                }
            });

            SecurityLoginUserVO loginUser = new SecurityLoginUserVO(sysUser,authorities);
            principal = loginUser;
            credentials = loginUser.getPassword();
        }

        //ldap账号,调用ldap账号认证服务(通过openfeign远程调用http接口)
        else if(AccountTypeEnum.LDAP_USER.getValue() == accountType){
            String token = u.getToken();
            String refreshToken = u.getRefreshToken();
            HttpServletResponseResultWrappers result = ldapFeignClient.
                    getPerson(userAccount,token,refreshToken,userAccount);
            //当调用http接口返回状态码不是200时，抛出自定义异常
            if(result.getCode() != HttpServletResponseCodeEnum.OK.getCode()){
                log.error("ldapFeignClient.getPerson 远程调用返回异常");
                log.error("异常结果:"+result);
                throw new BizException(result.getCode(),result.getMsg());
            }

            LdapPersonVO ldapPerson =  (LdapPersonVO)result.getData();
            //查询用户就是和权限数据
            List<LdapUserRolePermissionInfoVO> list = ldapPermissionService.listById(userAccount);
            HashSet<SimpleGrantedAuthority> finalAuthorities = authorities;
            list.stream().forEach(e->{
                if(e.getPermissionList() != null && e.getPermissionList().size() >0){
                    e.getPermissionList().stream().forEach(t->{
                        //设置权限
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(t.getUrl());
                        finalAuthorities.add(authority);
                    });
                }
            });

            SysLdapUser user = new SysLdapUser();
            user.setId((long) ldapPerson.getUid().hashCode());
            user.setUserId(ldapPerson.getUid());
            user.setUserName(ldapPerson.getCommonName());
            SecurityLdapLoginUserVO loginUser = new SecurityLdapLoginUserVO(user,ldapPerson.getUserPassword(),authorities);
            principal = loginUser;
            credentials = loginUser.getPassword();
        }

        //SpringSecurity内部使用ArrayList保持权限字符串,所以这里需要将HashSet转换成ArrayList
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>(authorities);
        //新建一个UsernamePasswordAuthenticationToken用来设置Authentication
        //UsernamePasswordAuthenticationToken由用户名、密码和角色(或者权限)组成
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, credentials, authorityList);
        //设置remoteAddr和sessionId
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * f里面有一个被拦截的url,里面调用MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法
     * 获取f对应的所有权限,再调用MyAccessDecisionManager的decide方法来校验用户的权限是否足够
     * @param f
     * @throws IOException
     * @throws ServletException
     */
    public void invoke(FilterInvocation f) throws IOException, ServletException {
        InterceptorStatusToken token = super.beforeInvocation(f);
        try {
             //执行下一个拦截器
            f.getChain().doFilter(f.getRequest(), f.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }



}
