package com.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * gitee oauth2.0第三方登录用户信息
 */
public class GiteeOauth2LoginUserVO {

    //用户名
    @Getter
    @Setter
    private String userName;

    //用户id
    @Getter
    @Setter
    private Integer userId;

    //登录账号名
    @Getter
    @Setter
    private String loginName;

    //用户头像地址
    @Getter
    @Setter
    private String avatarUrl;

    //仓库数
    @Getter
    @Setter
    private Integer publicRepos;

    private static String registrationId = "gitee";

    public static String getRegistrationId(){
        return registrationId;
    }


    /**
     * oauth2.0第三方登录用户的权限
     * /product/list/：商品查询权限
     * /product/add/：商品新增权限
     * /product/delete/ :商品删除权限
     * /product/update/： 商品修改权限
     * 注意：该权限暂时写死在代码中，建议从数据库中的权限表中动态获取!
     *
     */
    private static final String OAUTH2_USER_AUTHORITY =
            "/product/list/,/product/add/,/product/delete/,/product/update/";

    public static String getOauth2UserAuthority(){
        return OAUTH2_USER_AUTHORITY;
    }
}
