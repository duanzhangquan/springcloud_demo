package com.base.service.ldap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.entity.ldap.SysLdapUserRole;
import com.base.mapper.LdapUserRoleMapper;
import org.springframework.stereotype.Service;

@Service
public class LdapUserRoleService extends ServiceImpl<LdapUserRoleMapper, SysLdapUserRole> {
}
