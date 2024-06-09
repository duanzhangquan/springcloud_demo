package com.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.CustomTokenException;
import com.common.util.HttpServletResponseResultWrappers;
import com.common.util.EncryptionUtil;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.pojo.vo.UserTokenVO;
import com.sun.corba.se.impl.oa.toa.TOA;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j

/**
 * 需要解决的问题：
 * 1. token未携带请求头时invalidToken方法无法正确输出json数据导致异常
 */
public class MyGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().value();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        //1 uaa、oauth2和ldap这三个服务全部放行
        if (pathMatcher.match("/api/uaa/**", requestUrl)
                || pathMatcher.match("/api/ldap/**",requestUrl)
                || pathMatcher.match("/api/myOauth2/**",requestUrl)) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst(EncryptionUtil.USER_TOKEN_HEADER);
        String refreshToken = exchange.getRequest().getHeaders().getFirst(EncryptionUtil.USER_REFRESH_TOKEN_HEADER);
        String userAccount = exchange.getRequest().getHeaders().getFirst(EncryptionUtil.USER_ACCOUNT_HEADER);
        String accountType = exchange.getRequest().getHeaders().getFirst(EncryptionUtil.USER_ACCOUNT_TYPE_HEADER);
        if(StringUtils.isEmpty(userAccount)){
            String message = HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getMsg()+":account";
            HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.setMsg(message);
            return writeError(HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS,exchange,"");
        }
        if(StringUtils.isEmpty(accountType)){
            String message = HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.getMsg()+":accountType";
            HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS.setMsg(message);
            return writeError(HttpServletResponseCodeEnum.MISSING_HEADER_PARAMS,exchange,"");
        }
        try {
            //校验token
            UserTokenVO u = new UserTokenVO(token,refreshToken,userAccount);
            u.setAccountType(Integer.parseInt(accountType));
            EncryptionUtil.validateToken(u);
        }catch (CustomTokenException e){
            log.error("jwt token异常",e);
           return writeError(e.getBizResponseCodeEnum(),exchange,e.getData());
        }

        return chain.filter(exchange);
    }



    private Mono<Void> writeError(HttpServletResponseCodeEnum n, ServerWebExchange exchange,Object data){
        HttpServletResponseResultWrappers bizResponseResult =
                new HttpServletResponseResultWrappers(data, n.getCode(), n.getMsg());
        return buildHttpResponseResult(bizResponseResult, exchange);
    }


    private Mono<Void> buildHttpResponseResult(HttpServletResponseResultWrappers result, ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        byte[] bits = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.setStatusCode(HttpStatus.OK);
        //response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        response.getHeaders().add("Content-Type",
                "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public int getOrder() {
        return 0;
    }
}