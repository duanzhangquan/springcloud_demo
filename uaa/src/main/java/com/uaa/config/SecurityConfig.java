package com.uaa.config;

import com.common.exception.MyAccessDeniedExceptionHandler;
import com.common.exception.MyAuthenticationExceptionHandler;
import com.uaa.filter.CustomUsernamePasswordAuthenticationFilter;
import com.uaa.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;

/**
 * EnableGlobalMethodSecurity表示开启方法权限控制
 * @prePostEnabled属性表示在方法之前之前和执行之后进行权限检查
 */

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    /**
     * 1. url进行权限控制
     * 2. 配置过滤器
     * 3. 配置自定义异常处理器
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        jwtAuthenticationFilter.setUserDetailsService(userDetailsService);
        CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter =
                new CustomUsernamePasswordAuthenticationFilter(authenticationManager());

        http.cors().and().csrf().disable().authorizeRequests()
                // login接口允许匿名访问
                .antMatchers("/uaa/login").permitAll()
                // 不支持匿名访问
                .antMatchers("/uaa/test").authenticated()
                .and()

                .addFilter(customUsernamePasswordAuthenticationFilter)
                // 不需要session(无状态)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                //过滤器顺序：JwtAuthenticationFilter-->CustomUsernamePasswordAuthenticationFilter-->UsernamePasswordAuthenticationFilter
                .addFilterAt(customUsernamePasswordAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)
                // jwtAuthenticationFilter放到UsernamePasswordAuthenticationFilter的前面
                .addFilterBefore(jwtAuthenticationFilter, CustomUsernamePasswordAuthenticationFilter.class).

                formLogin()
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