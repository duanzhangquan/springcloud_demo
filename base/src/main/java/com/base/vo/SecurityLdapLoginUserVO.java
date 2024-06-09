package com.base.vo;

import com.base.entity.ldap.SysLdapUser;
import com.pojo.vo.ldap.LdapUserRolePermissionInfoVO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class SecurityLdapLoginUserVO implements UserDetails {

    private Long id;

    //对应ldap中的uid
    private String userId;
    //对应ldap中的cn
    private String username;
    //对应ldap中的userPassword
    private String userPassword;

    //用户对应的角色和权限列表
    private List<LdapUserRolePermissionInfoVO> rolePermissionInfoList;

    //通过springSecurity进行授权
    private Collection<? extends GrantedAuthority> authorities;

    public SecurityLdapLoginUserVO(SysLdapUser user, String userPassword, Collection<? extends GrantedAuthority> authorities) {
        id = user.getId();
        username = user.getUserName();
        userId = user.getUserId();
        this.userPassword = userPassword;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
