package com.pojo.dto;
import lombok.Data;

/**
 * 给用户绑定角色或解绑角色所需的入参
 */
@Data
public class BindUserRoleDTO {

    //用户id
    private Long userId;

    //角色id
    private Long roleId;

}
