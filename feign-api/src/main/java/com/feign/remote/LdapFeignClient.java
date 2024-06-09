package com.feign.remote;

import com.common.util.HttpServletResponseResultWrappers;
import com.pojo.vo.ldap.LdapPersonVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;


@FeignClient(value = "ldap")
public interface LdapFeignClient {

    /**
     * 根据userId查询ldap用户
     * @return
     */
    @GetMapping(value = "/ldap/getPerson/{uid}")
    @ResponseBody
    HttpServletResponseResultWrappers getPerson(@PathVariable("uid") String uid,
                                                              @RequestHeader("token") String token,
                                                              @RequestHeader("refreshToken") String refreshToken,
                                                              @RequestHeader("account") String account);



}
