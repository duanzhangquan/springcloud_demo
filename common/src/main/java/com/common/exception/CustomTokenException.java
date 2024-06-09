package com.common.exception;

import com.common.util.enumeration.HttpServletResponseCodeEnum;
import io.jsonwebtoken.JwtException;
import lombok.Getter;
import lombok.Setter;

/**
 * jwt token异常
 */
@Getter
@Setter
public class CustomTokenException extends JwtException {

    private HttpServletResponseCodeEnum bizResponseCodeEnum;
    private Object data;

    public CustomTokenException(String message){
        super(message);
    }

    public CustomTokenException(HttpServletResponseCodeEnum e){
        super(e.getMsg());
        this.bizResponseCodeEnum = e;
    }

    public CustomTokenException(HttpServletResponseCodeEnum e,Object data){
        super(e.getMsg());
        this.data = data;
        this.bizResponseCodeEnum = e;
    }

    public CustomTokenException(Throwable cause,HttpServletResponseCodeEnum e,Object data){
        super(e.getMsg(),cause);
        this.bizResponseCodeEnum = e;
        this.data = data;
    }


}
