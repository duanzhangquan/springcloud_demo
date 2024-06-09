package com.base.service.ldap;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.entity.ldap.SysLdapPermission;
import com.base.mapper.LdapPermissionMapper;
import com.pojo.vo.ldap.LdapUserRoleInfoVO;
import com.pojo.vo.ldap.LdapUserRolePermissionInfoVO;
import com.pojo.vo.ldap.SysLdapPermissionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LdapPermissionService extends ServiceImpl<LdapPermissionMapper, SysLdapPermission> {

    @Autowired
    private LdapPermissionMapper permissionMapper;
    @Autowired
    private LdapRoleService roleService;


    /**
     * 根据权限id查询权限列表
     * @param permissionIdList
     * @return
     */
    public List<SysLdapPermission> listById(List<Integer> permissionIdList){
        return permissionMapper.selectBatchIds(permissionIdList);
    }

    /**
     * 获取权限表中的所有权限数据
     * @return
     */
    public List<SysLdapPermission> listAll(){
        return permissionMapper.selectList(null);
    }
    /**
     * 根据用户id查询权限列表
     * @return
     */
    public List<LdapUserRolePermissionInfoVO> listById(String userId){
        List<LdapUserRolePermissionInfoVO> voList = new ArrayList<>();
        //可考虑将用户的角色和权限信息放入redis缓存中
        List<LdapUserRoleInfoVO> list = roleService.getUserRoleByUserId(userId);

        //遍历角色列表
        list.stream().forEach(e->{
            LdapUserRolePermissionInfoVO vo = new LdapUserRolePermissionInfoVO();
            BeanUtils.copyProperties(e,vo);
            String permissionIdList = e.getPermissionIdList();
            if(!StringUtils.isEmpty(permissionIdList)){
                //以,分割权限id字符串得到每个角色的权限id集合
                List<String> idListStr = Arrays.asList(permissionIdList.split(","));
                List<Integer> idIntList = idListStr.stream().map(Integer::parseInt).collect(Collectors.toList());
                List<SysLdapPermission> permissionList = this.listById(idIntList);
                String json = JSONObject.toJSONString(permissionList);
                List<SysLdapPermissionVO> p = JSONObject.parseArray(json,SysLdapPermissionVO.class);
                vo.setPermissionList(p);
                voList.add(vo);
            }
        });

        return voList;
    }

    public void addPermission(SysLdapPermission permission){
        super.save(permission);
    }

    public void deletePermission(Long id){
        super.removeById(id);
    }

    public void update(SysLdapPermission sysPermission){
        super.updateById(sysPermission);
    }

}
