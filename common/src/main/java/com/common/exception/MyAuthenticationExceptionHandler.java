package com.common.exception;

import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 处理AuthenticationException异常
 */
@Slf4j
public class MyAuthenticationExceptionHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.error("身份认证失败", e);
        int code = HttpServletResponseCodeEnum.AUTHENTICATION_FAIL.getCode();
        String msg = HttpServletResponseCodeEnum.AUTHENTICATION_FAIL.getMsg();
        HttpServletResponseResultWrappers bizResponseResult =  new HttpServletResponseResultWrappers("", code, msg);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(bizResponseResult);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
