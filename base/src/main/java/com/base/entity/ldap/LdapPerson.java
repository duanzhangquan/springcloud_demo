package com.base.entity.ldap;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

/**
 * ldap数据库实体映射
 * base = "ou=people"参数值绑定部门, dc=ldapTest,dc=com参数值绑定顶级域名
 * objectClasses可以理解为ldap中的数据结构类型，objectClass可分为以下3类：
 * (1) 结构型（Structural）
 *     如account、inetOrgPerson、person和organizationUnit
 * (2) 辅助型（Auxiliary）
 *     如extensibeObject
 * (3) 抽象型（Abstract）
 *     如top，抽象型的objectClass不能直接使用

 */

@Data
@Entry(base = "ou=people,dc=ldapTest,dc=com", objectClasses = "inetOrgPerson")
public class LdapPerson {

        @Id
        private Name id;

        /**
         * 全称surname,表示人员的姓氏
         */
        @Attribute(name = "sn")
        private String surName;

        /**
         * commonName（通用名称）用于指定对象的全名，通常用于表示人员、组织单位等实体的名称,
         * cn的值应该在entry条目中保持唯一。
         */
        @Attribute(name = "cn")
        private String commonName;

        /**
         * uid（用户标识符）用于指定用户的唯一标识符。uid的值通常是用户的登录名或其他唯一标识符。
         */
        @DnAttribute(value = "uid", index = 3)
        private String uid;

        /**
         * 用户的登录密码
         */
        @Attribute(name = "userPassword")
        private String userPassword;
}
