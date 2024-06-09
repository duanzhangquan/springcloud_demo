package com.gateway.config;

import com.alibaba.fastjson.JSON;
import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关层全局异常处理,返回统一格式的响应结果。
 * 使用Order注解强制获得最高异常处理优先级，
 * 然后使用bufferFactory.wrap方法传递自定义错误格式返回给前端
 */


@Slf4j
@Order(-1)
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.debug("处理网关异常",ex);
        ServerHttpResponse response = exchange.getResponse();
       /* if (response.isCommitted()) {
            return Mono.error(ex);
        }*/
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(HttpStatus.OK);
        }

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            HttpServletResponseCodeEnum e = HttpServletResponseCodeEnum.GATEWAY_EXCEPTION;
            e.setMsg(e.getMsg()+":"+ ex.getMessage());
            HttpServletResponseResultWrappers result =
                        new HttpServletResponseResultWrappers("", e.getCode(),e.getMsg());
            byte[] bits = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
            return bufferFactory.wrap(bits);
        }));
    }
}
