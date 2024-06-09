package com.pojo.vo.ldap;

import lombok.Data;

/**
 * ldap用户权限
 */
@Data
public class SysLdapPermissionVO {

    private Integer id;

    //权限名称
    private String name;

    //权限描述
    private String description;

    //授权url
    private String url;

    //父节点id
    private Integer pid;


    public SysLdapPermissionVO(Integer id, String name, String description, String url, Integer pid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.pid = pid;
    }
}
