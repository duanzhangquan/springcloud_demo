package com.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.entity.SysRole;
import com.pojo.vo.UserRoleInfoVO;

import java.util.List;

public interface RoleMapper extends BaseMapper<SysRole> {

    List<UserRoleInfoVO> getAllUserRole();

}
