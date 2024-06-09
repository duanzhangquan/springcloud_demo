package com.base.entity.ldap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_ldap_user_role")
@Data
public class SysLdapUserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    //用户id(外键)
    private Long userId;
    //角色id(外键)
    private Long roleId;
}
