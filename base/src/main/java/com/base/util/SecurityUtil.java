package com.base.util;
import com.base.vo.SecurityLoginUserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static SecurityLoginUserVO getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }

        Object p = auth.getPrincipal();
        if(p == null){
            return null;
        }
        if(!(p instanceof SecurityLoginUserVO)){
            return null;
        }
        return (SecurityLoginUserVO) p;
    }

}
