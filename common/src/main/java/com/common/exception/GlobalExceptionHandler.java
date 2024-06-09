package com.common.exception;

import com.alibaba.fastjson.JSONException;
import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.sql.SQLIntegrityConstraintViolationException;


/**
 * controller层全局异常捕获
 * @author： duanzhangquan
 */

@RestControllerAdvice(annotations = {RestController.class})
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public HttpServletResponseResultWrappers handler(Exception e) {
        HttpServletResponseResultWrappers bizResponseResult = null;

        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            int code = bizException.getBizResponseCodeEnum().getCode();
            String msg = bizException.getBizResponseCodeEnum().getMsg();
            log.error("业务运行异常", e);
            bizResponseResult =  new HttpServletResponseResultWrappers("", code, msg);
        }
        else if(e instanceof DataNotFoundException){
            log.error("数据未找到异常",e);
            DataNotFoundException DataNotFoundException = (DataNotFoundException) e;
            int code = DataNotFoundException.getBizResponseCodeEnum().getCode();
            bizResponseResult =  new HttpServletResponseResultWrappers("", code, e.getMessage());
        }
        else if(e instanceof IllegalHttpRequestException){
            log.error("非法http请求异常",e);
            IllegalHttpRequestException exception = (IllegalHttpRequestException) e;
            int code = exception.getBizResponseCodeEnum().getCode();
            bizResponseResult =  new HttpServletResponseResultWrappers("", code, e.getMessage());
        }
        else if (e instanceof JSONException) {
            log.error("json解析异常", e);
            int code = HttpServletResponseCodeEnum.JSON_ERROR.getCode();
            String msg = HttpServletResponseCodeEnum.JSON_ERROR.getMsg();
            bizResponseResult =  new HttpServletResponseResultWrappers("", code, msg);
        }
        else if(e instanceof MethodArgumentNotValidException){
            log.error("参数校验异常",e);
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException)e;
            int code = HttpServletResponseCodeEnum.ILLEGAL_PARAMS.getCode();

            BindingResult bindingResult = validException.getBindingResult();
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            bizResponseResult =  new HttpServletResponseResultWrappers("", code, errorMessage);
        }
        else if(e instanceof SQLIntegrityConstraintViolationException){
            log.error("违反唯一索引约束",e);
            int code = HttpServletResponseCodeEnum.DATA_REPLICATED.getCode();
            bizResponseResult =  new HttpServletResponseResultWrappers();
        }
        else if(e instanceof CustomDuplicateKeyException){
            log.error("违反唯一索引约束",e);
            CustomDuplicateKeyException exception = (CustomDuplicateKeyException)e;
            int code = exception.getBizResponseCodeEnum().getCode();
            String message = exception.getBizResponseCodeEnum().getMsg();
            bizResponseResult = new HttpServletResponseResultWrappers("",code,message);
        }
        else if(e instanceof JwtException){
            log.error("token已过期",e);
            int code = HttpServletResponseCodeEnum.TOKEN_EXPIRED.getCode();
            String msg = HttpServletResponseCodeEnum.TOKEN_EXPIRED.getMsg();
            bizResponseResult =  new HttpServletResponseResultWrappers("", code, msg);
        }
        else {
            log.error("程序异常", e);
            HttpServletResponseResultWrappers exceptionResult = new HttpServletResponseResultWrappers();
            exceptionResult.error();
            bizResponseResult =  exceptionResult;
        }

        return bizResponseResult;
    }

}

