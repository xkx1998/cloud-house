package com.xukexiang.house.user.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import org.apache.commons.lang.time.DateUtils;

import java.util.*;

/**
 * Token的生成和解析工具类
 */
public class JwtHelper {
    /**
     * 公用密钥，保存在服务端，客户端是不会知道密钥的，以防被攻击
     */
    public static final String SECRET = "session_secret";

    public static final String ISSUER = "Xukexiang";

    public static final int calendarInterval = 10;

    /**
     * 登录成功后生成Token
     *
     * @param claims
     * @return
     */
    public static String getToken(Map<String, String> claims) {
        try {
            // 签发时间
            Date iatDate = new Date();

            //过期时间 - 1天过期
            Calendar nowTime = Calendar.getInstance();
            nowTime.add(Calendar.DATE, 1);
            Date expireDate = nowTime.getTime();

            // 声明使用的算法
            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            // 声明头部：类型和使用的算法
            Map<String, Object> map = new HashMap<>();
            map.put("alg", "HS256");
            map.put("typ", "JWT");

            JWTCreator.Builder builder = JWT.create().withHeader(map) //header
                    .withIssuer(ISSUER) //issuer ,jwt的签发者
                    .withIssuedAt(iatDate) // 设置签发时间
                    .withExpiresAt(expireDate); // 设置过期时间

            //将claims设置到builder里面
            claims.forEach((k, v) -> builder.withClaim(k, v)); //payload

            return builder.sign(algorithm); //加密
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * 解密Token,最终存放的数据在JWT内部的实体claims里。它是存放数据的地方
     *
     * @param token
     * @return
     */
    public static Map<String, String> verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        DecodedJWT jwt = null;
        Map<String, String> resultMap = Maps.newHashMap();
        try {
            jwt = verifier.verify(token);
            Map<String, Claim> map = jwt.getClaims();
            map.forEach((k, v) -> resultMap.put(k, v.asString()));
        } catch (Exception e) {
            throw new RuntimeException("登录凭证过期，请重新登录");
        }
        return resultMap;
    }
}
