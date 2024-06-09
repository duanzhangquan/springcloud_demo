package com.common.util;

import com.common.exception.BizException;
import com.common.exception.CustomTokenException;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.pojo.vo.UserTokenVO;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 加解密工具类
 */
@Slf4j
public class EncryptionUtil {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String USER_TOKEN_HEADER = "token";
    public static final String USER_REFRESH_TOKEN_HEADER = "refreshToken";
    public static final String USER_ACCOUNT_HEADER = "account";
    public static final String USER_ACCOUNT_TYPE_HEADER = "accountType";


    //秘钥,使用AES加密
    private static String secretKey = "springcloud-demo867954t980@@j7654209";

    //token有效期12小时
    //private static final long EXPIRE_TIME = 12 * 3600 * 1000;
    //token有效期25分钟
    private static final long EXPIRE_TIME = 25 * 60 * 1000;

    //refreshToken有效时间7天
    //private static final long REFRESH_TIME = 7 * 24 * 3600 * 1000;
    //refreshToken有效时间35分钟
    private static final long REFRESH_TIME = 35 * 60 * 1000;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取token，返回Claims
     * @param token
     * @return
     */
    public static Claims getTokenClaims(String token) {
        token = token.replace(EncryptionUtil.TOKEN_PREFIX,"");
        SecretKey key = generalSecretKey();
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(key)         //设置密钥
                    .parseClaimsJws(token).getBody();//设置需要解析的jwt
            return claims;
        }catch (ExpiredJwtException e){
            log.error("token已过期",e);
            return null;
        }catch (Exception e){
            log.error("非法的token",e);
            throw new CustomTokenException(HttpServletResponseCodeEnum.TOKEN_ILLEGAL);
        }
    }

    /**
     * 校验token
     */
    public static void validateToken(String token){
        if(StringUtils.isEmpty(token)){
            log.error("缺少token参数");
            throw new CustomTokenException(HttpServletResponseCodeEnum.NONE_TOKEN_PARAMS);
        }

        if(!token.startsWith(EncryptionUtil.TOKEN_PREFIX)){
            log.error("未指定token授权类型参数");
            throw new CustomTokenException(HttpServletResponseCodeEnum.NONE_AUTH_TYPE);
        }

        if(getTokenClaims(token) == null){
            log.error("token已过期,请重新登录");
            throw new CustomTokenException(HttpServletResponseCodeEnum.TOKEN_EXPIRED);
        }
    }


    /**
     * 校验token和refreshToken
     * 如果refreshToken过期则提示用户重新登录，refreshToken未过期但是token过期了则输出"请刷新token"的提示，
     * 同时重新生成一个新的token和refreshToken，前端拿着新的token和refreshToken替换掉之前的旧token和旧refreshToken即可
     */
    public static void validateToken(UserTokenVO u){
        String token = u.getToken();
        String refreshToken = u.getRefreshToken();
        String account = u.getAccount();
        Integer accountType = u.getAccountType();

        if(StringUtils.isEmpty(token) || StringUtils.isEmpty(refreshToken)){
            log.error("缺少token或refreshToken参数");
            throw new CustomTokenException(HttpServletResponseCodeEnum.NONE_TOKEN_PARAMS,"");
        }

        if(!token.startsWith(EncryptionUtil.TOKEN_PREFIX) || !refreshToken.startsWith(EncryptionUtil.TOKEN_PREFIX)){
            log.error("未指定token授权类型参数");
            throw new CustomTokenException(HttpServletResponseCodeEnum.NONE_AUTH_TYPE,"");
        }

        //refreshToken过期
        if(getTokenClaims(refreshToken) == null){
            log.error("token已过期,请重新登录");
            throw new CustomTokenException(HttpServletResponseCodeEnum.TOKEN_EXPIRED,"");
        }
        //refreshToken未过期但token过期了
        if(getTokenClaims(token) == null){
            log.error("请刷新token");
            String newToken = EncryptionUtil.createToken(account,accountType);
            String newRefreshToken = EncryptionUtil.createRefreshToken(account);
            //重新生成新的token和新的刷新token
            u.setToken(EncryptionUtil.TOKEN_PREFIX + newToken);
            u.setRefreshToken(EncryptionUtil.TOKEN_PREFIX + newRefreshToken);
            throw new CustomTokenException(HttpServletResponseCodeEnum.TOKEN_NEED_REFRESH,u);
        }

    }

    /**
     * token解析,返回userAccount
     * @param token
     * @return
     */
    public static String parseToken(String token) {
        validateToken(token);

        token = token.replace(EncryptionUtil.TOKEN_PREFIX,"");
        SecretKey key = generalSecretKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)         //设置密钥
                .parseClaimsJws(token).getBody();//设置需要解析的jwt

        log.info("解析header中的token串:"+claims);
        return claims.get("userAccount",String.class);
    }


    /**
     * 生成一个新的token,token中保存了账号权限
     * 注意：jwt token的创建和解析非常耗时，需要优化！！
     * @param userAccount
     * @return
     */
    public static String createToken(String userAccount, HashSet<SimpleGrantedAuthority> authorities) {
        SecretKey secretKey = generalSecretKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + EXPIRE_TIME);
        Map<String,Object> claims = new HashMap<>();
        claims.put("userAccount",userAccount);
        claims.put("expirationDate",sdf.format(expirationDate));
        claims.put("authorities",authorities);

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(String.valueOf(nowMillis))
                .setSubject(userAccount)  //token的主体
                .setIssuedAt(now)  // 签发时间
                .signWith(SignatureAlgorithm.HS256, secretKey) //签名算法和密钥
                .setClaims(claims) //自定义的值
                .setExpiration(expirationDate); //设置token过期时间


        String token = builder.compact();
        log.debug("生成带有springSecurity权限的token:"+token);

        return token;
    }


    /**
     * 生成一个新的token
     * @param userAccount 登录账号
     * @param accountType  1:普通账号，2:ldap账号
     * @return
     */
    public static String createToken(String userAccount,Integer accountType) {
        SecretKey secretKey = generalSecretKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + EXPIRE_TIME);
        Map<String,Object> claims = new HashMap<>();
        claims.put("userAccount",userAccount);
        claims.put("expirationDate",sdf.format(expirationDate));
        claims.put("accountType",accountType);

        //添加accountType参数，标记用户的账号是ldap账号还是普通账号
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(String.valueOf(nowMillis))
                .setSubject(userAccount)  //token的主体
                .setIssuedAt(now)  // 签发时间
                .signWith(SignatureAlgorithm.HS256, secretKey) //签名算法和密钥
                .setClaims(claims) //自定义的值
                .setExpiration(expirationDate); //设置token过期时间

        String token = builder.compact();
        log.debug("生成token:"+token);

        return token;
    }



    /**
     * 生成一个新的RefreshToken
     * @return
     */
    public static String createRefreshToken(String userAccount) {
        SecretKey secretKey = generalSecretKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + REFRESH_TIME);
        Map<String,Object> claims = new HashMap<>();
        claims.put("userAccount",userAccount);
        claims.put("expirationDate",sdf.format(expirationDate));

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(String.valueOf(nowMillis))
                .setSubject(userAccount)  //token的主体
                .setIssuedAt(now)  // 签发时间
                .signWith(SignatureAlgorithm.HS256, secretKey) //签名算法和密钥
                .setClaims(claims) //自定义的值
                .setExpiration(expirationDate); //设置token过期时间

        String token = builder.compact();
        log.debug("生成refreshToken:"+token);

        return token;
    }

    /**
     * 刷新token
     *
     * @param account
     * @return {@link String}
     */
    public static String freshToken(String account,Integer accountType) {
        return createToken(account,accountType);
    }

    /**
     * 刷新freshToken
     *
     * @param account
     * @return {@link String}
     */
    public static String reFreshToken(String account) {
        return createRefreshToken(account);
    }


    /**
     * AES算法生成密钥
     * @return
     */
    public static SecretKey generalSecretKey(){
        byte[] encodedKey = Base64.getEncoder().encode(secretKey.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        return key;
    }

    /**
     * ascll字符数组转换为字符串
     * @param ascllArray
     * @return
     */
    public static String convertToString(String[] ascllArray) {
        byte[] utf8Bytes = new byte[ascllArray.length];
        for (int i = 0; i < ascllArray.length; i++) {
            utf8Bytes[i] = (byte) Integer.parseInt(ascllArray[i]);
        }
        return new String(utf8Bytes, StandardCharsets.UTF_8);
    }
}


