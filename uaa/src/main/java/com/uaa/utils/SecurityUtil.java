package com.uaa.utils;
import com.uaa.vo.LoginUserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static LoginUserVO getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }

        Object p = auth.getPrincipal();
        if(p == null){
            return null;
        }
        return (LoginUserVO) p;
    }
}
