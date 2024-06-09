package com.ldap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
/**
 * @author duan
 */
@SpringBootApplication
@ComponentScan(value = {"com.common","com.base","com.ldap"})
@EnableDiscoveryClient
public class LdapApplication {
    public static void main(String[] args) {

        SpringApplication.run(LdapApplication.class,args);
    }
}
