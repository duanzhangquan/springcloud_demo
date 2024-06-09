package com.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpServletResponseResultWriter {

    /**
     * 响应结果示例
     * {
     *     "code": 401,
     *     "msg": "身份认证失败",
     *     "data": null
     * }
     * @param response
     * @throws IOException
     */
    public static void writeOk(HttpServletResponse response,HttpServletResponseResultWrappers result) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(result));
    }

    public static void writeFailure(HttpServletResponse response,HttpServletResponseResultWrappers result) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(result));
    }
}
