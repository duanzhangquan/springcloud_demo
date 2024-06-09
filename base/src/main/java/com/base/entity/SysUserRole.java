package com.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class SysUserRole {
    @TableId(type = IdType.AUTO)
    private Long id;

    //用户id(外键)
    private Long userId;
    //角色id(外键)
    private Long roleId;

}
