package com.base.entity.ldap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_ldap_role")
@Data
public class SysLdapRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    //角色名
    private String code;

    //角色中文名
    private String nameCn;

    //权限id集合(外键),表示一个角色可以分配多个权限,该字段为空时表示角色未分配任何权限
    private String permissionIdList;

    //系统默认角色,不可删除,不可更改权限。1:是, 0:否
    private Integer defaultRole;

    //备注
    private String remark;
}
