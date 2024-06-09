package com.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HttpServletResponse返回结果统一包装类
 * @param <T>
 */
@Getter
@Setter
public class HttpServletResponseResultWrappers<T> {

    private int code;
    private String msg;
    private T data;

    public HttpServletResponseResultWrappers() {
    }

    public HttpServletResponseResultWrappers(T data) {
        this.code = HttpServletResponseCodeEnum.OK.getCode();
        this.msg = HttpServletResponseCodeEnum.OK.getMsg();
        this.data = data;
    }

    public void error() {
        this.code = HttpServletResponseCodeEnum.NOT_AVAILABILITY.getCode();
        this.msg = HttpServletResponseCodeEnum.NOT_AVAILABILITY.getMsg();
    }

    public HttpServletResponseResultWrappers(T data, int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
