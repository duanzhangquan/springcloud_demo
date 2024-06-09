package com.common.util.enumeration;

import lombok.Getter;

/**
 * springSecurity授权枚举
 */
public enum SpringSecurityPermissionEnum {

    ROLE_ANONYMOUS("role_anonymous", "允许匿名身份访问"),
    ACCESS_ALLOW("access_allow", "允许访问"),
    ACCESS_DENIED("access_denied","权限不足,拒绝访问");

    @Getter
    private String val;

    @Getter
    private String description;

    SpringSecurityPermissionEnum(String val, String description) {
        this.val = val;
        this.description = description;
    }
}
