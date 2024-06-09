package com.ldap.config;

import com.common.exception.MyAccessDeniedExceptionHandler;
import com.common.exception.MyAuthenticationExceptionHandler;
import com.ldap.filter.CustomUsernamePasswordAuthenticationFilter;
import com.ldap.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class LdapSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("ldapUserDetailService")
    UserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=people")
                .contextSource()

                 //设置嵌入式ldap服务器的访问地址
                .url("ldap://localhost:8700/dc=ldapTest,dc=com")
                .and()
                .passwordCompare()
               // .passwordEncoder(bCryptPasswordEncoder())
                //指定ldap的密码字段
                .passwordAttribute("userPassword");

        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        jwtAuthenticationFilter.setUserDetailsService(userDetailsService);
        CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter =
                new CustomUsernamePasswordAuthenticationFilter(authenticationManager());
        http.cors().and().csrf().disable()
                .authorizeRequests()
                // login接口允许匿名访问
                .antMatchers("/ldap/login").permitAll()
                //其它接口必须登录后才能访问
                .anyRequest().authenticated()
                .and()
                .addFilter(customUsernamePasswordAuthenticationFilter)
                // 不需要session(无状态)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                //过滤器顺序：JwtAuthenticationFilter-->CustomUsernamePasswordAuthenticationFilter-->UsernamePasswordAuthenticationFilter
                .addFilterAt(customUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // jwtAuthenticationFilter放到UsernamePasswordAuthenticationFilter的前面
                .addFilterBefore(jwtAuthenticationFilter, CustomUsernamePasswordAuthenticationFilter.class)

                .formLogin()
                //修改默认的登录接口入参
                .usernameParameter("account");

        //自定义异常处理器
        http.exceptionHandling().accessDeniedHandler(new MyAccessDeniedExceptionHandler())
                .authenticationEntryPoint(new MyAuthenticationExceptionHandler());
    }

    /**
     * 使用BCrypt对密码加密
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 跨域配置
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}

