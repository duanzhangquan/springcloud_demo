package com.uaa.service;

import com.alibaba.nacos.api.utils.StringUtils;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.uaa.utils.SecurityUtil;
import com.uaa.vo.LoginUserVO;
import org.springframework.stereotype.Service;

/**
 * 自定义权限实现
 */
@Service("permissionService")
public class PermissionService {
    public boolean hasPer(String permission) {
        if (StringUtils.isBlank(permission)){
            return false;
        }

        LoginUserVO loginUser = SecurityUtil.getLoginUser();
        if (loginUser == null || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        return loginUser.getPermissions().contains(StringUtils.trim(permission));
    }
}
