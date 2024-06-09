package com.base.service;

import com.base.entity.SysUser;
import com.base.entity.ldap.SysLdapUser;
import com.base.service.ldap.LdapPersonService;
import com.base.service.security.LdapUserDetailService;
import com.base.service.security.MyUserDetailService;
import com.base.vo.SecurityLdapLoginUserVO;
import com.base.vo.SecurityLoginUserVO;
import com.common.exception.CustomTokenException;
import com.common.util.EncryptionUtil;
import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.HttpServletResponseResultWriter;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.pojo.vo.UserTokenVO;
import com.pojo.vo.ldap.LdapPersonVO;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Service
@Slf4j
public class CustomAuthenticationService {

    @Autowired
    private UserService userService;
    private LdapPersonService ldapPersonService;

    public void setLdapPersonService(LdapPersonService ldapPersonService){
        this.ldapPersonService = ldapPersonService;
    }




    /**
     * 校验用户，包括token和refreshToken、account等三个参数
     */
    public boolean validateUser(UserTokenVO userTokenVO, HttpServletResponse response) throws IOException {
        log.debug("校验用户:",userTokenVO);
        String userAccount = userTokenVO.getAccount();
        String token = userTokenVO.getToken();

        //校验account,account请求头也是必须传入的,因为要根据account来生成token和refreshToken
        if(StringUtils.isEmpty(userAccount)){
            int code =  HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getCode();
            String message = HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getMsg()+":account";
            log.error(message);
            HttpServletResponseResultWrappers r = new HttpServletResponseResultWrappers("",code,message);
            HttpServletResponseResultWriter.writeOk(response,r);
            return false;
        }
        try{
            //校验token和refreshToken
            EncryptionUtil.validateToken(userTokenVO);
        }
        catch (CustomTokenException e){
            int code = e.getBizResponseCodeEnum().getCode();
            String message = e.getBizResponseCodeEnum().getMsg();
            log.error(message);
            HttpServletResponseResultWrappers r = new HttpServletResponseResultWrappers(e.getData(),code,message);
            HttpServletResponseResultWriter.writeOk(response,r);
            return false;
        }

        //校验前端传入的account是否与token中的account匹配
        String tokenSubject = EncryptionUtil.parseToken(token);
        if(!userAccount.equals(tokenSubject)){
            int code = HttpServletResponseCodeEnum.ACCOUNT_NO_MATCH_TOKEN.getCode();
            String message = HttpServletResponseCodeEnum.ACCOUNT_NO_MATCH_TOKEN.getMsg();
            log.error(message);
            HttpServletResponseResultWrappers r = new HttpServletResponseResultWrappers("",code,message);
            HttpServletResponseResultWriter.writeOk(response,r);
            return false;
        }

        return true;
    }

    /**
     * token校验通过后设置用户为已认证状态
     */
    public void setAuthentication(String userAccount,UserDetailsService userDetailsService){
        Object principal = null;
        UserDetails userDetails = userDetailsService.loadUserByUsername(userAccount);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String credentials = userDetails.getPassword();

        if(userDetails instanceof SecurityLoginUserVO ){
            SysUser sysUser = userService.getByAccount(userAccount);
            SecurityLoginUserVO loginUser = new SecurityLoginUserVO(sysUser,authorities);
            principal = loginUser;
        }
        else if(userDetails instanceof SecurityLdapLoginUserVO){
            LdapPersonVO person = null;
            try {
                person = ldapPersonService.getPerson(userAccount);
            }catch (EmptyResultDataAccessException e){
                log.error("用户不存在:", userAccount);
                throw new UsernameNotFoundException("用户不存在,"+userAccount,e);
            }

            Long id = (long) person.getUid().hashCode();
            SysLdapUser user = new SysLdapUser(id,person.getCommonName(),person.getUid());
            SecurityLdapLoginUserVO loginUser = new SecurityLdapLoginUserVO(user,person.getUserPassword(),authorities);
            principal = loginUser;
        }

        log.error("principal:"+principal);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal,credentials, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.debug("token校验成功,设置用户为已认证状态");
    }
}
