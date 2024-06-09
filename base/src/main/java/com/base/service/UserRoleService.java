package com.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pojo.dto.BindUserRoleDTO;
import com.base.entity.SysUserRole;
import com.base.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserRoleService extends ServiceImpl<UserRoleMapper, SysUserRole> {

    /**
     * 给用户绑定一个或多个角色
     */
    public void bindRole(List<BindUserRoleDTO> dto){
        List<SysUserRole> list = new ArrayList<>();
        dto.stream().forEach(e ->{
            SysUserRole userRole = new SysUserRole();
            userRole.setRoleId(e.getRoleId());
            userRole.setUserId(e.getUserId());

            list.add(userRole);
        });

        if(list.size() >0){
            super.saveBatch(list);
        }
    }

    /**
     * 解绑用户的一个或多个角色
     */
    public void unBindRole(List<BindUserRoleDTO> dto){
        List<SysUserRole> list = new ArrayList<>();
        dto.stream().forEach(e ->{
            SysUserRole userRole = new SysUserRole();
            userRole.setRoleId(e.getRoleId());
            userRole.setUserId(e.getUserId());

            list.add(userRole);
        });

        list.stream().forEach(e->{
            Map<String,Object> map = new HashMap<>();
            map.put("user_id",e.getUserId());
            map.put("role_id",e.getRoleId());
            super.removeByMap(map);
        });
    }
}
