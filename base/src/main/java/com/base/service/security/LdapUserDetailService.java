package com.base.service.security;

import com.base.entity.ldap.LdapPerson;
import com.base.entity.ldap.SysLdapUser;
import com.base.service.ldap.LdapPermissionService;
import com.base.service.ldap.LdapPersonService;
import com.base.vo.SecurityLdapLoginUserVO;
import com.pojo.vo.ldap.LdapPersonVO;
import com.pojo.vo.ldap.LdapUserRolePermissionInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class LdapUserDetailService implements UserDetailsService {

    @Autowired
    private LdapPersonService ldapPersonService;
    @Autowired
    private LdapPermissionService permissionService;


    /**
     * 从ldao-server.ldif库中查询用户
     * @param userId
     * @return
     * @throws UsernameNotFoundException
     * 该方法最终由 org.springframework.security.authentication.dao.
     * DaoAuthenticationProvider#retrieveUser(java.lang.String,
     * org.springframework.security.authentication.UsernamePasswordAuthenticationToken)方法回调
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        LdapPersonVO person = null;
        try {
            person = ldapPersonService.getPerson(userId);
        }catch (EmptyResultDataAccessException e){
            log.error("用户不存在:", userId);
            throw new UsernameNotFoundException("用户不存在,"+userId,e);
        }

        List<LdapUserRolePermissionInfoVO> list = permissionService.listById(userId);
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        list.stream().forEach(e->{
            if(e.getPermissionList() != null && e.getPermissionList().size() >0){
                e.getPermissionList().stream().forEach(t->{
                    //设置权限
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(t.getUrl());
                    //多个不同的角色可能被分配相同的权限,所以需要使用set去重
                    authorities.add(authority);
                });
            }
        });

        SysLdapUser user = new SysLdapUser();
        user.setId((long) person.getUid().hashCode());
        user.setUserId(person.getUid());
        user.setUserName(person.getCommonName());
        return new SecurityLdapLoginUserVO(user,person.getUserPassword(),authorities);
    }
}
