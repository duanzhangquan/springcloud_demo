package com.base.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.entity.SysRole;
import com.base.mapper.RoleMapper;
import com.pojo.vo.UserRoleInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService extends ServiceImpl<RoleMapper,SysRole> {

    @Autowired
    private RoleMapper roleMapper;
    /**
     * 获取所有用户的角色列表
     * @return
     */
    public List<UserRoleInfoVO> getUserRole(){
       return roleMapper.getAllUserRole();
    }

    /**
     * 根据用户id查询用户的角色列表
     * @param userId
     * @return
     */
    public List<UserRoleInfoVO> getUserRoleByUserId(long userId){
        List<UserRoleInfoVO> list = roleMapper.getAllUserRole();
        list = list.stream().filter(s->s.getUserId().equals(userId)).collect(Collectors.toList());

        return list;
    }

    public void addRole(SysRole role){
        super.save(role);
    }

    public void deleteRole(Long id){
        super.removeById(id);
    }

    public void update(SysRole role){
        super.updateById(role);
    }
}
