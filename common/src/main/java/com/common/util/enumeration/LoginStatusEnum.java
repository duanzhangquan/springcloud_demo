package com.common.util.enumeration;

import lombok.Getter;

public enum  LoginStatusEnum {

    ONLINE("在线", 1),
    OFFLINE("离线",0);

    @Getter
    private String status;

    @Getter
    private int code;

    LoginStatusEnum(String status, int code) {
        this.status = status;
        this.code = code;
    }
}
