package com.base.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author duan
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    //用户中文名
    private String username;

    //账号名
    private String account;

    //密码
    private String password;

    //是否系统默认初始化用户,不可删除和更改,1:是 0:否
    private Integer defaultUser;


    public SysUser(Long id, String username, String account, String password, Integer defaultUser) {
        this.id = id;
        this.username = username;
        this.account = account;
        this.password = password;
        this.defaultUser = defaultUser;
    }

    public SysUser(){}

}