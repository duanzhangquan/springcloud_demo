package com.common.util.enumeration;

import lombok.Getter;
import lombok.Setter;

public enum HttpServletResponseCodeEnum {
    OK("SUCCESS", 200),

    GATEWAY_EXCEPTION("网关异常",501),
    NOT_AVAILABILITY("服务暂时不可用", 502),
    NOT_FOUND_API_URL("api地址不存在",503),

    ILLEGAL_PARAMS("非法http请求参数", 420),
    MISSING_HEADER_PARAMS("缺少必须的请求头参数",421),

    AUTHENTICATION_FAIL("身份认证失败",401),
    ACCESS_DENIED("权限不足,拒绝访问",403),

    DATA_NOT_FOUND("数据不存在",422),
    DATA_REPLICATED("数据重复", 423),

    USER_PASSWORD_ERROR("密码不正确",319),
    USER_NOT_EXIST("用户不存在", 320),

    ILLEGAL_REQUEST("非法http请求",321),

    NONE_TOKEN_PARAMS("缺少token或refreshToken参数",322),
    NONE_AUTH_TYPE("未指定token授权类型参数",323),
    TOKEN_EXPIRED("token已过期,请重新登录",324),
    TOKEN_ILLEGAL("非法的token",325),
    TOKEN_NEED_REFRESH("请刷新token",326),
    ACCOUNT_NO_MATCH_TOKEN("account与token不匹配",327),

    JSON_ERROR("json数据格式不正确", 220);

    @Getter
    @Setter
    private String msg;

    @Getter
    private int code;

    HttpServletResponseCodeEnum(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }


}
