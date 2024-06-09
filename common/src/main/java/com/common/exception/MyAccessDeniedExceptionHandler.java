package com.common.exception;

import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 处理AccessDeniedException异常
 */
@Slf4j
public class MyAccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        log.error("权限不足,拒绝访问", e);
        int code = HttpServletResponseCodeEnum.ACCESS_DENIED.getCode();
        String msg = HttpServletResponseCodeEnum.ACCESS_DENIED.getMsg();
        HttpServletResponseResultWrappers bizResponseResult =  new HttpServletResponseResultWrappers("", code, msg);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(bizResponseResult);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
