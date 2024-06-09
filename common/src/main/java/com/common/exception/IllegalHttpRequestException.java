package com.common.exception;

import com.common.util.enumeration.HttpServletResponseCodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName IilgalHttpRequestException
 * @Description 非法http请求异常
 * @Author duanzhangquan
 * @Version 1.0
 **/
@Getter
@Setter
public class IllegalHttpRequestException extends RuntimeException {

    private HttpServletResponseCodeEnum bizResponseCodeEnum;

    public IllegalHttpRequestException() {
        super();
    }


    public IllegalHttpRequestException(HttpServletResponseCodeEnum bizResponseCodeEnum) {
        super(bizResponseCodeEnum.getMsg());
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }

    public IllegalHttpRequestException(HttpServletResponseCodeEnum bizResponseCodeEnum, String msg) {
        super(msg);
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }

    public IllegalHttpRequestException(HttpServletResponseCodeEnum bizResponseCodeEnum, Throwable cause) {
        super(cause);
        this.bizResponseCodeEnum = bizResponseCodeEnum;
    }


    public IllegalHttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
