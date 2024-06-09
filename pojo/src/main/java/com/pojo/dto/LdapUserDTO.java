package com.pojo.dto;

import lombok.Data;

/**
 * ldap系统登录用户入参
 */
@Data
public class LdapUserDTO {

    private String account;
    private String password;
    private Integer rememberMe;
}
