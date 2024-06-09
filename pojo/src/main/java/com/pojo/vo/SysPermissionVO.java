package com.pojo.vo;

import lombok.Data;

@Data
public class SysPermissionVO {

    private int id;

    //权限名称
    private String name;

    //权限描述
    private String description;

    //授权url
    private String url;

    //父节点id
    private int pid;
}
