package com.oauth2;

import com.base.service.ldap.LdapPersonService;
import com.base.service.security.LdapUserDetailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 注意:不要直接启动该类,要以spring-boot:run命令方式启动才行,否则404!!!
 */
@SpringBootApplication
@ComponentScan(value = {"com.base","com.oauth2"},
        //排除ldap相关的bean
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {LdapPersonService.class}),
              @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {LdapAutoConfiguration.class}),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {LdapUserDetailService.class})
        })

//扫描feign-api模块下的包
@EnableFeignClients(basePackages = "com.feign.remote")
public class OAuth2Application {
    public static void main(String[] args) {
        SpringApplication.run(OAuth2Application.class,args);
    }
}
