package com.oauth2.config;

import com.common.exception.MyAccessDeniedExceptionHandler;
import com.common.exception.MyAuthenticationExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;


/**
 * 1. 两个核心的过滤器
 * OAuth2AuthorizationRequestRedirectFilter:重定向到Github提供的授权页面
 * OAuth2LoginAuthenticationFilter:认证请求,授权成功后跳转URL
 *
 * 2. 授权登录流程
 *  2.1 访问授权登录地址:
 *      http://localhost:8608/myOauth2/login/oauth2/code/gitee地址(需要提前在github中配置好),
 *      OAuth2AuthorizationRequestRedirectFilter会过滤掉这个地址然后重定向到Github提供的授权页面
 *
 *  2.2 调用 本机的/oauth/test接口获取登录的用户信息
 */
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {


    //生成默认的登录页的过滤器
    //DefaultLoginPageGeneratingFilter d;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        HttpSecurity h;
        http
                .sessionManagement()
                //session策略
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and().oauth2Login().permitAll()
                .and()
                .authorizeRequests()
                //放行url
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/login").permitAll()
                //注销接口放行
                .and().logout().permitAll()
                //其它请求都需要登录后才可访问
                .and().authorizeRequests().anyRequest().authenticated().
                and()
                //允许跨域
                .cors()
                //表单登录
               // .and().formLogin()
                //oauth2登录
                .and().oauth2Login(Customizer.withDefaults())
                //关闭跨站请求防护
                .csrf().disable();


      /*  http.exceptionHandling().accessDeniedHandler(new MyAccessDeniedExceptionHandler())
                .authenticationEntryPoint(new MyAuthenticationExceptionHandler());*/
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.clientRegistration());
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration
                // 唯一标志
                .withRegistrationId("gitee")
                // 申请应用的clientId
                .clientId("db0cbaa723ac14bca9113908e4b5b03b50009489aa5293b172da8baa4b7386f4")
                // 申请应用的clientSecret
                .clientSecret("3228811883ba2d0cb5ff81adc01f5cdb37041d57a4336984ba21c09134f95780")
                // 授权方式 授权码
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("http://localhost:8608/myOauth2/login/oauth2/code/gitee")
                // 请求授权范围,可在github文档中查看
                //.scope("repo")
                // 申请授权地址
                .authorizationUri("https://gitee.com/oauth/authorize")
                // 获取token地址
                .tokenUri("https://gitee.com/oauth/token")
                // 获取用户信息接口
                .userInfoUri("https://gitee.com/api/v5/user")
                // 返回信息中用户名的字段
                .userNameAttributeName("name")
                .build();
    }

    /**
     * 跨域配置
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}





