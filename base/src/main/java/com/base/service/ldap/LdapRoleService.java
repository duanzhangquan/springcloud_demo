package com.base.service.ldap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.entity.ldap.SysLdapRole;
import com.base.mapper.LdapRoleMapper;
import com.pojo.vo.ldap.LdapUserRoleInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LdapRoleService extends ServiceImpl<LdapRoleMapper, SysLdapRole> {


    @Autowired
    private LdapRoleMapper roleMapper;
    /**
     * 获取所有用户的角色列表
     * @return
     */
    public List<LdapUserRoleInfoVO> getUserRole(){
        return roleMapper.getAllUserRole();
    }

    /**
     * 根据用户id查询用户的角色列表
     * @param userId
     * @return
     */
    public List<LdapUserRoleInfoVO> getUserRoleByUserId(String userId){
        List<LdapUserRoleInfoVO> list = roleMapper.getAllUserRole();
        list = list.stream().filter(s->s.getUserId().equals(userId)).collect(Collectors.toList());

        return list;
    }

    public void addRole(SysLdapRole role){
        super.save(role);
    }

    public void deleteRole(Long id){
        super.removeById(id);
    }

    public void update(SysLdapRole role){
        super.updateById(role);
    }
}
