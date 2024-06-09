package com.pojo.vo;

import lombok.Data;

/**
 * 用户角色、权限信息
 */
@Data
public class UserRoleInfoVO {

    private Long userId;

    private String account;

    private String userName;

    //角色中文名
    private String roleName;

    //角色名
    private String roleCode;

    //角色关联的权限id列表
    private String permissionIdList;
}
