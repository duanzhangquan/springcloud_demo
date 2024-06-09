package com.base.entity.ldap;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_ldap_user")
@Data
public class SysLdapUser {

    private Long id;

    private String userName;

    private String userId;

    public SysLdapUser(Long id, String userName, String userId) {
        this.id = id;
        this.userName = userName;
        this.userId = userId;
    }
    public SysLdapUser(){}

}
