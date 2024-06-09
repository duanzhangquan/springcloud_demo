package com.common.exception;

import com.common.util.enumeration.HttpServletResponseCodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 违反数据库唯一索引时抛出的异常
 */
@Getter
@Setter
public class CustomDuplicateKeyException extends BizException {

    public CustomDuplicateKeyException(){
        super();
    }

    public CustomDuplicateKeyException(int code,String msg){
        super(code,msg);
    }


    public CustomDuplicateKeyException(HttpServletResponseCodeEnum bizResponseCodeEnum){
        super(bizResponseCodeEnum);
    }
}
