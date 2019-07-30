package com.xukexiang.house.api.service;

import com.google.common.collect.Lists;
import com.xukexiang.house.api.dao.UserDao;
import com.xukexiang.house.api.model.User;
import com.xukexiang.house.api.utils.BeanHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private FileService fileService;

    @Value("${domain.name}")
    private String domainName;

    @Autowired
    private UserDao userDao;

    /**
     * 根据email判断用户是否存在
     *
     * @param email
     * @return
     */
    public boolean isExist(String email) {
        return getUser(email) != null;
    }

    private User getUser(String email) {
        User queryUser = new User();
        queryUser.setEmail(email);
        List<User> users = getUserByQuery(queryUser);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    private List<User> getUserByQuery(User queryUser) {
        return userDao.getUserList(queryUser);
    }

    /**
     * 添加用户
     *
     * @param account
     * @return
     */
    public boolean addAccount(User account) {
        if (account.getAvatarFile() != null) {
            List<String> imags = fileService.getImgPaths(Lists.newArrayList(account.getAvatarFile()));
            account.setAvatar(imags.get(0));
        }
        account.setEnableUrl("http://" + domainName + "/accounts/verify");
        BeanHelper.setDefaultProp(account, User.class);
        userDao.addUser(account);
        return true;
    }

    /**
     * 激活用户
     *
     * @param key
     * @return
     */
    public boolean enable(String key) {
        return userDao.enable(key);
    }

    /**
     * 认证用户名密码并生成TOKEN
     *
     * @param username
     * @param password
     * @return
     */
    public User auth(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }
        User user = new User();
        user.setEmail(username);
        user.setPasswd(password);
        try {
            user = userDao.authUser(user);
        } catch (Exception e) {
            return null;
        }
        return user;
    }

    /**
     * 注销
     *
     * @param token
     */
    public void logout(String token) {
        userDao.logout(token);
    }

    /**
     * 发送重置密码邮件
     * @param email
     */
    public void remember(String email) {
        userDao.resetNotify(email, "http://" + domainName + "/accounts/reset");
    }

    /**
     * 获取重置密码邮箱
     * @param key
     * @return
     */
    public String getResetEmail(String key) {
        String email = userDao.getEmail(key);
        return email;
    }

    /**
     * 重置密码
     *
     * @param key
     * @param passwd
     * @return
     */
    public User reset(String key, String passwd) {
        return userDao.reset(key, passwd);
    }

    /**
     * 更新个人信息
     *
     * @param updateUser
     * @return
     */
    public User updateUser(User updateUser) {
        BeanHelper.onUpdate(updateUser);
        return userDao.updateUser(updateUser);
    }
}
