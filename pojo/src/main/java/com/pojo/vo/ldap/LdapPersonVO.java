package com.pojo.vo.ldap;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.naming.Name;

/**
 * LdapPerson实体类vo
 */
@Data
public class LdapPersonVO {

    //ldap唯一标识符
    private String id;

    //对应ldap中的sn字段，全称surname,表示人员的姓氏
    private String surName;

    // 对应ldap中的cn字段 ,全称commonName（通用名称）用于指定对象的全名，通常用于表示人员、组织单位等实体的名称,
    // cn的值应该在entry条目中保持唯一。
    private String commonName;

    //对应ldap中的uid字段（用户标识符）用于指定用户的唯一标识符。uid的值通常是用户的登录名或其他唯一标识符。
    private String uid;

    //用户的登录密码，对应ldap中的userPassword字段
    private String userPassword;
}
