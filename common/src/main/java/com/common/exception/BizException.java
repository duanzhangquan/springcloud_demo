package com.common.exception;

import com.common.util.enumeration.HttpServletResponseCodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常
 */

@Getter
@Setter
public class BizException extends RuntimeException {

    private HttpServletResponseCodeEnum bizResponseCodeEnum;
    private int code;
    private String msg;

    public BizException() {
        super();
    }

    public BizException(int code,String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;

    }

    public BizException(HttpServletResponseCodeEnum bizResponseCodeEnum) {
        super(bizResponseCodeEnum.getMsg());
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }

    public BizException(HttpServletResponseCodeEnum bizResponseCodeEnum, String msg) {
        super(msg);
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }

    public BizException(HttpServletResponseCodeEnum bizResponseCodeEnum, Throwable cause) {
        super(cause);
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }


    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
