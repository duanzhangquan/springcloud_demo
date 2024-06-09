package com.common.exception;

import com.common.util.enumeration.HttpServletResponseCodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据未找到异常
 */
@Getter
@Setter
public class DataNotFoundException extends RuntimeException {

    private HttpServletResponseCodeEnum bizResponseCodeEnum;

    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(HttpServletResponseCodeEnum bizResponseCodeEnum) {
        super(bizResponseCodeEnum.getMsg());
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }

    public DataNotFoundException(HttpServletResponseCodeEnum bizResponseCodeEnum, String msg) {
        super(msg);
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }

    public DataNotFoundException(HttpServletResponseCodeEnum bizResponseCodeEnum, Throwable cause) {
        super(cause);
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }


    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
