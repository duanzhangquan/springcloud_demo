//package com.uaa.interceptor;

import com.common.util.HttpServletResponseResultWrappers;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * controller层返回结果统一包装
 */
/*
@RestControllerAdvice(annotations = {RestController.class})
public class HttpServletResponseResultInterceptor implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return (AnnotatedElementUtils.hasAnnotation(methodParameter.getContainingClass(), ResponseBody.class) ||
                methodParameter.hasMethodAnnotation(ResponseBody.class));
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

        String returnTypeName = methodParameter.getParameterType().getName();
        if (returnTypeName.equals("void")) {
            return new HttpServletResponseResultWrappers("SUCCESS");
        }
        else {
            // controller层必须返回Map或javabean，只有map或javabean才能表示为json数据格式,
            // 而单独的String无法表示json格式,会抛出类型转换异常
            return new HttpServletResponseResultWrappers(body);
        }
    }
}
*/
