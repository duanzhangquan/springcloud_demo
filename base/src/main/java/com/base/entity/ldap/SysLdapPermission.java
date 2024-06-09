package com.base.entity.ldap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_ldap_permission")
@Data
public class SysLdapPermission {

    @TableId(type = IdType.AUTO)
    private int id;

    //权限名称
    private String name;

    //权限描述
    private String description;

    //授权url
    private String url;

    //父节点id
    private int pid;
}
