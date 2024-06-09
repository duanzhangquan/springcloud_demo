package com.pojo.vo.ldap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ldap用户角色、权限信息
 */
@Data
@NoArgsConstructor
public class LdapUserRolePermissionInfoVO {

    //对应ldap中的uid
    private String userId;

    //对应ldap中的cn
    private String userName;

    //角色中文名
    private String roleName;

    //角色名
    private String roleCode;

    //角色关联的权限对象列表
    private List<SysLdapPermissionVO> permissionList;

    public LdapUserRolePermissionInfoVO(String userId, String userName, String roleName, String roleCode) {
        this.userId = userId;
        this.userName = userName;
        this.roleName = roleName;
        this.roleCode = roleCode;
    }
}
