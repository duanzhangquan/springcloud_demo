package com.base.vo;

import com.base.entity.SysRole;
import com.base.entity.SysUser;
import com.pojo.vo.UserRolePermissionInfoVO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author duan
 */
@Data
public class SecurityLoginUserVO implements UserDetails {

    private Long id;
    private String username;
    private String account;
    private String password;

    //用户对应的角色和权限列表
    private List<UserRolePermissionInfoVO> rolePermissionInfoList;

    //通过自定义方式进行授权
    private Set<String> permissions = new HashSet<>();

    //通过springSecurity进行授权
    private Collection<? extends GrantedAuthority> authorities;

    public SecurityLoginUserVO(SysUser user, Collection<? extends GrantedAuthority> authorities) {
        id = user.getId();
        username = user.getUsername();
        account = user.getAccount();
        password = user.getPassword();
        this.authorities = authorities;
    }

    public SecurityLoginUserVO(SysUser user, SysRole role) {
        id = user.getId();
        username = user.getUsername();
        account = user.getAccount();
        password = user.getPassword();
        permissions.add(role.getCode());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
