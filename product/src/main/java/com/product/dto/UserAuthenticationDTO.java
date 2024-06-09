package com.product.dto;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.List;

@Data
public class UserAuthenticationDTO {

    //账号类型 1:普通账号  2:ldap账号
    private Integer accountType;

    //登录账号
    private String userAccount;

    //springSecurity权限
    private HashSet<SimpleGrantedAuthority> authorities;
}
