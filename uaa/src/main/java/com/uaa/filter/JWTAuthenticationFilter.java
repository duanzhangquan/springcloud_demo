package com.uaa.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uaa.dto.UserDTO;
import com.uaa.utils.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 用户登录时对用户名密码进行校验的过滤器，在token校验过滤器之后执行。在该过滤器中会对用户名密码进行比对，
 * 校验成功后返回一个token给客户端，下次客户端访问时在请求头带上此token代表该用户已经被认证。
 */

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private ThreadLocal<Integer> rememberMe = new ThreadLocal<>();
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        // 从输入流中获取到登录的信息
        try {
            UserDTO dto = new ObjectMapper().readValue(request.getInputStream(), UserDTO.class);
            rememberMe.set(dto.getRememberMe() == null ? 0 : dto.getRememberMe());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword(), new ArrayList<>())
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {

        UserDTO dto = (UserDTO) authResult.getPrincipal();
        log.info("loginUser",dto);
   /*    boolean isRemember = rememberMe.get() == 1;
         String role = "";*/

        String token = EncryptionUtil.createToken(dto.getUsername());
        /* 返回创建成功的token
         但是这里创建的token只是单纯的token
         按照jwt的规定，最后请求的时候应该是 `Bearer token`*/
        response.setHeader("token", EncryptionUtil.TOKEN_PREFIX + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.getWriter().write("authentication failed, reason: " + failed.getMessage());
    }
}
