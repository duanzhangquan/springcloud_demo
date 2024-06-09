package com.uaa;

import com.base.service.ldap.LdapPersonService;
import com.base.service.security.LdapUserDetailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author duan
 * com.common:扫描maven common模块下的Component注解的Bean
 * com.base: 扫描maven base模块下的Component注解的Bean
 */
@ComponentScan(value = {"com.common","com.base","com.uaa"},
        //排除ldap相关的bean
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {LdapPersonService.class}),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {LdapAutoConfiguration.class}),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {LdapUserDetailService.class})
        })
@SpringBootApplication
@EnableDiscoveryClient
public class UaaApplication {

    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class,args);
    }
}
