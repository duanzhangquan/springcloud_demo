package com.product.config;
import com.common.exception.MyAccessDeniedExceptionHandler;
import com.common.exception.MyAuthenticationExceptionHandler;
import com.product.interceptor.MyFilterSecurityInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;


/**
 * EnableGlobalMethodSecurity表示开启方法权限控制
 * @prePostEnabled属性表示在方法之前之前和执行之后进行权限检查
 */

@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
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
        //所有过滤器的排序在HttpSecurity.performBuild方法中可以查看到
        http.cors().and().csrf().disable()
                //禁用表单登录
                .formLogin().disable();
        http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class)
                //不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                //.and()
                //放行url
               // .authorizeRequests().antMatchers("/oauth/product/list").permitAll();
       // http.addFilterBefore(jwtAuthenticationTokenFilter,MyFilterSecurityInterceptor.class);
        //http.addFilterAfter(filterSecurityInterceptor,MyFilterSecurityInterceptor.class);
        //自定义异常处理器
        http.exceptionHandling().accessDeniedHandler(new MyAccessDeniedExceptionHandler())
                .authenticationEntryPoint(new MyAuthenticationExceptionHandler());
    }

    /**
     * 跨域配置
     * @return
     */
 /*   @Bean
    CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }*/
}