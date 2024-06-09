package com.oauth2.service;

import com.alibaba.fastjson.JSONObject;
import com.common.util.EncryptionUtil;
import com.common.util.enumeration.AccountTypeEnum;
import com.pojo.vo.GiteeOauth2LoginUserVO;
import com.pojo.dto.GiteeOauth2UserIdentifierDTO;
import com.pojo.vo.UserTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class GiteeOauth2UserService {

    /**
     * 创建oauth2.0用户的token和refreshToken并保存至session中
     * @param u
     */
    public void createOauth2UserToken(GiteeOauth2UserIdentifierDTO u, HttpServletRequest request){
        HttpSession session = request.getSession();
        //将userName和userId、registrationId三个字段组合在一起表示为一个唯一的oauth2.0 account
        String oauth2UserAccount = JSONObject.toJSONString(u);
        HashSet<SimpleGrantedAuthority> authorities = addOauth2UserAuthority();
        String token = EncryptionUtil.createToken(oauth2UserAccount,authorities);
        String refreshToken = EncryptionUtil.createRefreshToken(oauth2UserAccount);

        session.setAttribute("token",token);
        session.setAttribute("refreshToken",refreshToken);
        session.setAttribute("account",oauth2UserAccount);
    }

    /**
     * 给当前账号添加springSecurity权限
     * @return
     */
    private HashSet<SimpleGrantedAuthority> addOauth2UserAuthority(){
        HashSet<SimpleGrantedAuthority> authorities = new HashSet<>();

        //以,号分割多个权限并封装到SimpleGrantedAuthority集合中
        List<String> authList = Arrays.asList(GiteeOauth2LoginUserVO.getOauth2UserAuthority().split(","));
        authList.stream().forEach(e->{
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(e);
            authorities.add(authority);
        });

        return authorities;
    }

    /**
     * 获取gitee登录用户信息
     * @return
     */
    public GiteeOauth2LoginUserVO getLoginUserInfo(){
        //DefaultOAuth2User中保存了第三方用户登录的session,该session是springSecurity在用户登录成功后添加的
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal();
        log.debug("oAuth2User:"+oAuth2User);
        GiteeOauth2LoginUserVO oauth2LoginUser = new GiteeOauth2LoginUserVO();
        oauth2LoginUser.setUserId(oAuth2User.getAttribute("id"));
        oauth2LoginUser.setLoginName(oAuth2User.getAttribute("login"));
        oauth2LoginUser.setUserName(oAuth2User.getAttribute("name"));
        oauth2LoginUser.setAvatarUrl(oAuth2User.getAttribute("avatar_url"));
        oauth2LoginUser.setPublicRepos(oAuth2User.getAttribute("public_repos"));

        return oauth2LoginUser;
    }

    /**
     * openfeign调用之前的参数准备
     */
    public UserTokenVO  feignInvokeParamsPrepare(HttpServletRequest request){
        UserTokenVO  userTokenVO = new UserTokenVO();
        HttpSession session = request.getSession();
        Object token = session.getAttribute("token");
        Object refreshToken = session.getAttribute("refreshToken");
        Object account = session.getAttribute("account");

        //token和refreshToken、account都为空时,说明用户是直接访问的/product/list或product/add等接口，在这个接口中设置session
        if(StringUtils.isEmpty(token) && StringUtils.isEmpty(refreshToken) && StringUtils.isEmpty(account)){
            GiteeOauth2LoginUserVO oAuth2User =  this.getLoginUserInfo();
            GiteeOauth2UserIdentifierDTO u = new GiteeOauth2UserIdentifierDTO(oAuth2User.getUserName(),oAuth2User.getUserId(),
                    GiteeOauth2LoginUserVO.getRegistrationId());
            this.createOauth2UserToken(u,request);
            token = EncryptionUtil.TOKEN_PREFIX + session.getAttribute("token");
            refreshToken = EncryptionUtil.TOKEN_PREFIX + session.getAttribute("refreshToken");
            account = session.getAttribute("account");
        }
        //否则说明用户进入了地址为"/”的主页面接口，在该接口中已经设置了session,直接获取session即可
        else{
            token = EncryptionUtil.TOKEN_PREFIX + token;
            refreshToken = EncryptionUtil.TOKEN_PREFIX + refreshToken;
        }

        Integer accountType = AccountTypeEnum.OAUTH2_USER.getValue();
        userTokenVO.setToken(token.toString());
        userTokenVO.setRefreshToken(refreshToken.toString());
        userTokenVO.setAccount(account.toString());
        userTokenVO.setAccountType(accountType);

        return userTokenVO;
    }
}
