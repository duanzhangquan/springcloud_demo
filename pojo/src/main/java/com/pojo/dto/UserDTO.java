package com.pojo.dto;

import lombok.Data;

/**
 * 普通方式登录用户入参
 */
@Data
public class UserDTO {
    private String account;
    private String password;
    private Integer rememberMe;
}