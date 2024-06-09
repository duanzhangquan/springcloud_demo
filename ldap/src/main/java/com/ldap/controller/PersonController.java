package com.ldap.controller;

import com.base.service.ldap.LdapPersonService;
import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.pojo.vo.ldap.LdapPersonVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class PersonController {

    @Autowired
    private LdapPersonService ldapPersonService;

    /**
     * 根据uid查询ldap用户
     *
     * @param uid
     * @return
     */
    @GetMapping(value = "/getPerson/{uid}")
    @ResponseBody
    public HttpServletResponseResultWrappers getPerson(@PathVariable("uid") String uid) {
        try {
            HttpServletResponseResultWrappers<LdapPersonVO> result =
                    new HttpServletResponseResultWrappers(ldapPersonService.getPerson(uid));
            return result;
        } catch (EmptyResultDataAccessException e) {
            log.error("用户不存在:"+uid,e);
            HttpServletResponseCodeEnum responseCodeEnum = HttpServletResponseCodeEnum.USER_NOT_EXIST;
            int code = responseCodeEnum.getCode();
            String msg = responseCodeEnum.getMsg();
            HttpServletResponseResultWrappers result = new HttpServletResponseResultWrappers("", code, msg);
            return result;
        }
    }


    /**
     * 根据uid查询ldap用户
     * @param uid
     * @return
     */
/*    @GetMapping(value = "/getPerson/{uid}")
    public LdapPersonVO getPerson(@PathVariable("uid") String uid){
        return ldapPersonService.getPerson(uid);
    }
*/

}
