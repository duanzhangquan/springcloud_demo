package com.common.util.enumeration;

import lombok.Getter;

public enum AccountTypeEnum {

    NORMAL_USER(1, "普通用户"),
    LDAP_USER(2,"ldap用户"),
    OAUTH2_USER(3,"oauth2.0第三方用户");

    @Getter
    private int value;

    @Getter
    private String type;

    AccountTypeEnum(int value, String type) {
        this.value = value;
        this.type = type;
    }
}
