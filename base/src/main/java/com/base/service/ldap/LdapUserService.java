package com.base.service.ldap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.entity.ldap.SysLdapUser;
import com.base.mapper.LdapUserMapper;
import org.springframework.stereotype.Service;

@Service
public class LdapUserService extends ServiceImpl<LdapUserMapper, SysLdapUser> {
}
