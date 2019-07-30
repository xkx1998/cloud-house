package com.xukexiang.house.api.dao;

import com.xukexiang.house.api.common.ListResponse;
import com.xukexiang.house.api.common.RestResponse;
import com.xukexiang.house.api.config.GenericRest;
import com.xukexiang.house.api.model.Agency;
import com.xukexiang.house.api.model.User;
import com.xukexiang.house.api.utils.Rests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {
    @Autowired
    private GenericRest rest;

//    public String getusername(Long id) {
//        String url = "http://provider-user/getusername?id=" + id;
//        RestResponse<String> response = rest.get(url, new ParameterizedTypeReference<RestResponse<String>>() {
//        }).getBody();
//
//        return response.getResult();
//    }

    @Value("${user.service.name}")
    private String userServiceName;

    /**
     * 获取用户列表
     *
     * @param query
     * @return
     */
    public List<User> getUserList(User query) {
        ResponseEntity<RestResponse<List<User>>> resultEntity = rest.post("http://" + userServiceName + "/user/getList", query,
                new ParameterizedTypeReference<RestResponse<List<User>>>() {
                });
        RestResponse<List<User>> restResponse = resultEntity.getBody();
        if (restResponse.getCode() == 0) {
            return restResponse.getResult();
        } else {
            return null;
        }
    }

    /**
     * 添加用户
     *
     * @param account
     * @return
     */
    public User addUser(User account) {
        String url = "http://" + userServiceName + "/user/add";
        ResponseEntity<RestResponse<User>> responseEntity = rest.post(url, account, new ParameterizedTypeReference<RestResponse<User>>() {
        });
        RestResponse<User> response = responseEntity.getBody();
        if (response.getCode() == 0) {
            return response.getResult();
        } else {
            throw new IllegalStateException("Can not add user");
        }
    }

    /**
     * 激活
     *
     * @param key
     * @return
     */
    public boolean enable(String key) {
        String url = "http://" + userServiceName + "/user/enable?key=" + key;
        RestResponse<Object> response = rest.get(url, new ParameterizedTypeReference<RestResponse<Object>>() {
        }).getBody();
        return response.getCode() == 0;
    }

    /**
     * 登录验证token
     *
     * @param user
     * @return
     */
    public User authUser(User user) {
        String url = "http://" + userServiceName + "/user/auth";
        ResponseEntity<RestResponse<User>> responseEntity = rest.post(url, user, new ParameterizedTypeReference<RestResponse<User>>() {
        });
        RestResponse<User> response = responseEntity.getBody();
        if (response.getCode() == 0) {
            return response.getResult();
        } else {
            throw new IllegalStateException("Can not add user");
        }
    }

    /**
     * 注销
     *
     * @param token
     */
    public void logout(String token) {
        String url = "http://" + userServiceName + "/user/logout?token=" + token;
        rest.get(url, new ParameterizedTypeReference<RestResponse<Object>>() {
        });
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public User getUserByToken(String token) {
        String url = "http://" + userServiceName + "/user/get?token=" + token;
        ResponseEntity<RestResponse<User>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<User>>() {
        });
        RestResponse<User> response = responseEntity.getBody();
        if (response == null || response.getCode() != 0) {
            return null;
        }
        return response.getResult();
    }

    public void resetNotify(String email, String url) {
        Rests.exc(() -> {
            String sendUrl = Rests.toUrl(userServiceName, "/user/resetNotify?email=" + email + "&url=" + url);
            rest.get(sendUrl, new ParameterizedTypeReference<RestResponse<Object>>() {
            });
            return new Object();
        });
    }


    public String getEmail(String key) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/user/getKeyEmail?key=" + key);
            ResponseEntity<RestResponse<String>> responseEntity =
                    rest.get(url, new ParameterizedTypeReference<RestResponse<String>>() {
                    });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 重置密码
     *
     * @param key
     * @param passwd
     * @return
     */
    public User reset(String key, String passwd) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/user/reset?key=" + key + "&password=" + passwd);
            ResponseEntity<RestResponse<User>> responseEntity =
                    rest.get(url, new ParameterizedTypeReference<RestResponse<User>>() {
                    });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 获取经纪机构列表
     *
     * @return
     */
    public List<Agency> getAllAgency() {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/list");
            ResponseEntity<RestResponse<List<Agency>>> responseEntity =
                    rest.get(url, new ParameterizedTypeReference<RestResponse<List<Agency>>>() {
                    });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 更新个人信息
     *
     * @param updateUser
     * @return
     */
    public User updateUser(User updateUser) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/user/update");
            ResponseEntity<RestResponse<User>> responseEntity =
                    rest.post(url, updateUser, new ParameterizedTypeReference<RestResponse<User>>() {
                    });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 获取经纪人详情
     *
     * @param id
     * @return
     */
    public User getAgentById(Long id) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/agentDetail?id=" + id);
            ResponseEntity<RestResponse<User>> responseEntity =
                    rest.get(url, new ParameterizedTypeReference<RestResponse<User>>() {
                    });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 获取经济机构详情
     *
     * @param id
     * @return
     */
    public Agency getAgencyById(Integer id) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/agencyDetail?id=" + id);
            ResponseEntity<RestResponse<Agency>> responseEntity =
                    rest.get(url, new ParameterizedTypeReference<RestResponse<Agency>>() {
                    });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 添加经纪机构
     *
     * @param agency
     */
    public void addAgency(Agency agency) {
        Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/add");
            ResponseEntity<RestResponse<Object>> responseEntity =
                    rest.post(url, agency, new ParameterizedTypeReference<RestResponse<Object>>() {
                    });
            return responseEntity.getBody();
        });
    }

    /**
     * 获取经济人列表
     *
     * @param limit
     * @param offset
     * @return
     */
    public ListResponse<User> getAgentList(Integer limit, Integer offset) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(userServiceName, "/agency/agentList?limit=" + limit + "&offset=" + offset);
            ResponseEntity<RestResponse<ListResponse<User>>> responseEntity =
                    rest.get(url, new ParameterizedTypeReference<RestResponse<ListResponse<User>>>() {
                    });
            return responseEntity.getBody();
        }).getResult();
    }
}
