package com.xukexiang.house.user.controller;

import com.xukexiang.house.user.common.RestResponse;
import com.xukexiang.house.user.model.User;
import com.xukexiang.house.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("user")
public class UserController {
    @Value("${server.port}")
    private Integer port;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserController.class);

    //-------------------------查询-----------------------------

    /**
     * 根据UserId查询
     * @param id
     * @return
     */
    @RequestMapping("getById")
    public RestResponse<User> getUserById(Long id) {
        User user = userService.getUserById(id);
        return RestResponse.success(user);
    }

    /**
     * 获取用户列表
     * @param user
     * @return
     */
    @RequestMapping("getList")
    public RestResponse<List<User>> getUserList(@RequestBody User user) {
        List<User> users = userService.getUserByQuery(user);
        return RestResponse.success(users);
    }

    //------------------------注册-------------------------------

    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping("add")
    public RestResponse<User> add(@RequestBody User user) {
        userService.addAccount(user, user.getEnableUrl());
        return RestResponse.success();
    }

    /**
     * 激活用户
     * @param key
     * @return
     */
    @RequestMapping("enable")
    public RestResponse<User> enable(String key) {
        userService.enable(key);
        return RestResponse.success();
    }

    //------------------------登录/鉴权----------------------------

    /**
     * 登录验证并生成token
     * @param user
     * @return
     */
    @RequestMapping("auth")
    public RestResponse<User> auth(@RequestBody User user) {
        User finalUser = userService.auth(user.getEmail(), user.getPasswd());
        return RestResponse.success(finalUser);
    }

    /**
     * 服务端解析token
     * @param token
     * @return
     */
    @RequestMapping("get")
    public RestResponse<User> getUser(String token) {
        User finalUser = userService.getLoginedUserByToken(token);
        return RestResponse.success(finalUser);
    }

    /**
     * 注销
     * @param token
     * @return
     */
    @RequestMapping("logout")
    public RestResponse<Object> logout(String token) {
        userService.invalidate(token);
        return RestResponse.success();
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @RequestMapping("update")
    public RestResponse<User> update(@RequestBody User user){
        User updateUser = userService.updateUser(user);
        return RestResponse.success(updateUser);
    }

    /**
     * 发送重置密码邮件，并将Key和email的关系存放到redis
     * @param email
     * @param url
     * @return
     */
    @RequestMapping("resetNotify")
    public RestResponse<User> resetNotify(String email,String url){
        userService.resetNotify(email,url);
        return RestResponse.success();
    }

    /**
     * 重置密码
     * @param key
     * @param password
     * @return
     */
    @RequestMapping("reset")
    public RestResponse<User> reset(String key ,String password){
        User updateUser = userService.reset(key,password);
        return RestResponse.success(updateUser);
    }

    /**
     * 根据key在redis中获取email
     * @param key
     * @return
     */
    @RequestMapping("getKeyEmail")
    public RestResponse<String> getKeyEmail(String key){
        String  email = userService.getResetKeyEmail(key);
        return RestResponse.success(email);
    }
}
