package com.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @Description:
 * @Author: duanzhangquan
 */
@EnableWebFluxSecurity
@Configuration
public class GatewaySecurityConfig {


    @Bean
    public SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable();
        return http.build();
    }


}
