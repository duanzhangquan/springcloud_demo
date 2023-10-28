package com.uaa.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author duan
 */
@Data
@TableName("user")
public class User {

    private Long id;
    private String username;
    private String password;
    private String permission;
    private String role;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", permission='" + permission + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}