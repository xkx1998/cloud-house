package com.xukexiang.house.user.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.xukexiang.house.user.common.UserException;
import com.xukexiang.house.user.common.UserException.Type;
import com.xukexiang.house.user.mapper.UserMapper;
import com.xukexiang.house.user.model.User;
import com.xukexiang.house.user.utils.BeanHelper;
import com.xukexiang.house.user.utils.HashUtils;
import com.xukexiang.house.user.utils.JwtHelper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    @Value("${images.prefix}")
    private String imgPrefix;

    /**
     * 1.首先通过缓存获取
     * 2.不存在才通过数据库获取用户对象
     * 3.将用户对象写入缓存，设置缓存时间5分钟
     * 4.返回对象
     *
     * @param id
     * @return
     */
    public User getUserById(Long id) {
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        User user = null;
        if (Strings.isNullOrEmpty(json)) {
            user = userMapper.selectById(id);
            user.setAvatar(imgPrefix + user.getAvatar());
            String str = JSON.toJSONString(user);
            redisTemplate.opsForValue().set(key, str);
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);

        } else {
            user = JSON.parseObject(json, User.class);
        }
        return user;
    }

    /**
     * 查询所有用户
     *
     * @param user
     * @return
     */
    public List<User> getUserByQuery(User user) {
        List<User> users = userMapper.select(user);
        users.forEach(u -> {
            u.setAvatar(imgPrefix + u.getAvatar());
        });
        return users;
    }

    /**
     * 注册：添加用户
     *
     * @param user
     * @param enableUrl
     */
    public void addAccount(User user, String enableUrl) {
        user.setPasswd(HashUtils.encryPassword(user.getPasswd()));
        BeanHelper.onInsert(user);
        userMapper.insert(user);
        registerNotify(user.getEmail(), enableUrl);
    }

    /**
     * 发送email给新注册用户激活
     *
     * @param email
     * @param enableUrl
     */
    private void registerNotify(String email, String enableUrl) {
        String randomKey = HashUtils.hashString(email) + RandomStringUtils.randomAlphabetic(10);
        redisTemplate.opsForValue().set(randomKey, email);
        String content = enableUrl + "?key=" + randomKey;
        mailService.sendSimpleMail("房产平台激活邮件", content, email);
    }

    /**
     * 激活用户,用redis缓存email和key的关系
     *
     * @param key
     * @return
     */
    public boolean enable(String key) {
        String email = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(email)) {
            throw new UserException(Type.USER_NOT_FOUND, "无效的key");
        }
        User updateUser = new User();
        updateUser.setEmail(email);
        updateUser.setEnable(1);
        userMapper.update(updateUser);
        return true;
    }

    /**
     * 校验用户名密码，并生成token
     *
     * @param email
     * @param passwd
     * @return
     */
    public User auth(String email, String passwd) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(passwd)) {
            throw new UserException(Type.USER_AUTH_FAIL, "User Auth Fail");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswd(HashUtils.encryPassword(passwd));
        user.setEnable(1);
        List<User> list = getUserByQuery(user);
        if (!list.isEmpty()) {
            User retUser = list.get(0);
            onLogin(retUser);
            return retUser;
        }
        throw new UserException(Type.USER_AUTH_FAIL, "User Auth Fail");
    }

    /**
     * 生成token
     *
     * @param user
     */
    private void onLogin(User user) {
        String token = JwtHelper.getToken(ImmutableMap.of("email", user.getEmail()
                , "name", user.getName()
                , "ts", Instant.now().getEpochSecond() + ""));
        renewToken(token, user.getEmail());
        user.setToken(token);

    }

    /**
     * 更新redis中token的失效时间
     *
     * @param token
     * @param email
     * @return
     */
    private String renewToken(String token, String email) {
        redisTemplate.opsForValue().set(email, token);
        redisTemplate.expire(email, 30, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 鉴权
     *
     * @param token
     * @return
     */
    public User getLoginedUserByToken(String token) {
        Map<String, String> map = null;
        try {
            map = JwtHelper.verifyToken(token);
        } catch (Exception e) {
            // token 校验失败, 抛出Token验证非法异常
            throw new UserException(Type.USER_NOT_LOGIN, "User not Login");
        }

        String email = map.get("email");
        Long expired = redisTemplate.getExpire(email);
        //若token的过期时间大于0
        if (expired > 0) {
            renewToken(token, email);
            User user = getUserByEmail(email);
            user.setToken(token);
            return user;

        }
        throw new UserException(Type.USER_NOT_LOGIN, "User not Login");
    }

    /**
     * 根据Email查询User
     *
     * @param email
     * @return
     */
    private User getUserByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        List<User> users = getUserByQuery(user);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        throw new UserException(Type.USER_NOT_LOGIN, "User not found for " + email);
    }

    public void invalidate(String token) {
        Map<String, String> map = JwtHelper.verifyToken(token);
        redisTemplate.delete(map.get("email"));
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    public User updateUser(User user) {
        if (user.getEmail() == null) {
            return null;
        }
        if (!Strings.isNullOrEmpty(user.getPasswd()) ) {
            user.setPasswd(HashUtils.encryPassword(user.getPasswd()));
        }
        userMapper.update(user);
        return userMapper.selectByEmail(user.getEmail());
    }

    /**
     * 发送重置密码邮件，并将Key和email的关系存放到redis
     * @param email
     * @param url
     */
    public void resetNotify(String email, String url) {
        String randomKey = "reset_" + RandomStringUtils.randomAlphabetic(10);
        redisTemplate.opsForValue().set(randomKey, email);
        redisTemplate.expire(randomKey, 1,TimeUnit.HOURS);
        String content = url +"?key="+  randomKey;
        mailService.sendSimpleMail("房产平台重置密码邮件", content, email);
    }

    public String getResetKeyEmail(String key) {
        return  redisTemplate.opsForValue().get(key);
    }

    /**
     * 重置密码
     * @param key
     * @param password
     * @return
     */
    public User reset(String key, String password) {
        String email = getResetKeyEmail(key);
        User updateUser = new User();
        updateUser.setEmail(email);
        updateUser.setPasswd(HashUtils.encryPassword(password));
        userMapper.update(updateUser);
        return getUserByEmail(email);
    }
}
