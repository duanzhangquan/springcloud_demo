package com.pojo.vo;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 双token机制，认证成功后返回给前端两个token
 */
@Data
public class UserTokenVO {
    //token
    private String token;

    //刷新token
    private String refreshToken;

    //登录账号
    private String account;

    //账号类型 1:普通账号 2:ldap账号
    private Integer accountType;

    public UserTokenVO(){}

    public UserTokenVO(String token, String refreshToken,String account) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.account = account;
    }


    public UserTokenVO(String token, String refreshToken, String account, Integer accountType) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.account = account;
        this.accountType = accountType;
    }
}
