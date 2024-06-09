package com.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.entity.ldap.SysLdapRole;
import com.pojo.vo.ldap.LdapUserRoleInfoVO;

import java.util.List;

public interface LdapRoleMapper extends BaseMapper<SysLdapRole> {

    List<LdapUserRoleInfoVO> getAllUserRole();
}
