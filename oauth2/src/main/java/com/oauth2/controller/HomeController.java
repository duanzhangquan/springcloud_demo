package com.oauth2.controller;
import com.alibaba.fastjson.JSONObject;
import com.oauth2.service.GiteeOauth2UserService;
import com.pojo.dto.GiteeOauth2UserIdentifierDTO;
import com.pojo.vo.GiteeOauth2LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


@Controller
@Slf4j
public class HomeController {

    @Autowired
    private GiteeOauth2UserService giteeOauth2UserService;
    @Autowired
    private GiteeOauth2UserService tokenService;

    @GetMapping("/hello")
    @ResponseBody
    public JSONObject hello(Principal principal, HttpServletRequest request) {
        log.debug("test hello");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("principal", principal.getName());
        return jsonObject;
    }

    /**
     * 登录成功后进入的页面(gitee中配置的"应用主页"也是这个地址)
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String  index(Model model, HttpServletRequest request) {
        log.debug("---home---");
        GiteeOauth2LoginUserVO loginUser  = giteeOauth2UserService.getLoginUserInfo();
        GiteeOauth2UserIdentifierDTO u = new GiteeOauth2UserIdentifierDTO(
                loginUser.getUserName(),loginUser.getUserId(),loginUser.getRegistrationId());
        tokenService.createOauth2UserToken(u,request);

        model.addAttribute("userName",loginUser.getUserName());
        model.addAttribute("loginName",loginUser.getLoginName());
        model.addAttribute("userId",loginUser.getUserId());
        model.addAttribute("avatarUrl",loginUser.getAvatarUrl());
        model.addAttribute("publicRepos", loginUser.getPublicRepos());
        return "home";
    }


    /**
     * 解决登录成功后报404错误问题
     * @return
     */

    @GetMapping(value = "/login/")
    public String redirect(){
        log.debug("重定向到首页");
        return "redirect:/?loginSuccess";
    }

}
