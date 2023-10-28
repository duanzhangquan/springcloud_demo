package com.uaa.utils;

import com.uaa.dto.UserDTO;
import com.uaa.entity.User;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 加解密工具类
 */
@Slf4j
public class EncryptionUtil {

    public static final String TOKEN_HEADER = "token";
    public static final String TOKEN_PREFIX = "Bearer ";

    //待加密的key,使用AES加密
    private static String secretKey = "springcloud-demo867954t980@@j7654209";
    //token过期时间,有效期2天,单位毫秒
    private static long tokenExpired = 172800000;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static boolean isExpiration(String token) {
        SecretKey key = generalSecretKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)         //设置密钥
                .parseClaimsJws(token).getBody();//设置需要解析的jwt

        long nowMillis = System.currentTimeMillis();
        long expiration = claims.getExpiration().getTime();
        return nowMillis > expiration;
    }


    public static String getUsername(String token) {
        SecretKey key = generalSecretKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)         //设置密钥
                .parseClaimsJws(token).getBody();//设置需要解析的jwt

        log.info("解析header中的token串:"+claims);
        return claims.get("userName",String.class);
    }


    public static String createToken(String userName) {
        SecretKey secretKey = generalSecretKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + tokenExpired);

        Map<String,Object> claims = new HashMap<>();
        claims.put("userName",userName);
        claims.put("expirationDate",sdf.format(expirationDate));

        JwtBuilder builder = Jwts.builder()
                .setId(String.valueOf(nowMillis))
                .setSubject(userName)  //token的主体
                .setIssuedAt(now)  // 签发时间
                .signWith(SignatureAlgorithm.HS256, secretKey) //签名算法和密钥
                .setClaims(claims); //自定义的值
        builder.setExpiration(expirationDate); //设置token过期时间
        String token = builder.compact();
        log.debug("生成token:"+token);

        return token;
    }

    /**
     * AES算法生成密钥
     * @return
     */
    public static SecretKey generalSecretKey(){
        byte[] encodedKey = Base64.decodeBase64(secretKey);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        return key;
    }
}


