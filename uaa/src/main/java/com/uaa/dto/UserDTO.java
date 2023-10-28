package com.uaa.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private Integer rememberMe;
}