package com.pojo.vo.ldap;

import lombok.Data;

/**
 * ldap用户角色、权限信息
 */
@Data
public class LdapUserRoleInfoVO {
    private Long id;

    private String userId;

    private String userName;

    //角色中文名
    private String roleName;

    //角色名
    private String roleCode;

    //角色关联的权限id列表
    private String permissionIdList;
}
