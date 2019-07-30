package com.xukexiang.house.api.dao;

import java.util.List;

import com.xukexiang.house.api.common.HouseUserType;
import com.xukexiang.house.api.common.ListResponse;
import com.xukexiang.house.api.common.RestResponse;
import com.xukexiang.house.api.config.GenericRest;
import com.xukexiang.house.api.model.*;
import com.xukexiang.house.api.utils.Rests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;


@Repository
public class HouseDao {

    @Autowired
    private GenericRest rest;

    @Value("${house.service.name}")
    private String houseServiceName;

    /**
     * 获取所有城市
     * @return
     */
    public List<City> getAllCitys() {
        RestResponse<List<City>> resp = Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/allCitys");
            ResponseEntity<RestResponse<List<City>>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<List<City>>>() {
            });
            return responseEntity.getBody();
        });
        return resp.getResult();
    }

    /**
     * 获取所有小区
     * @return
     */
    public List<Community> getAllCommunitys() {
        RestResponse<List<Community>> resp = Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/allCommunitys");
            ResponseEntity<RestResponse<List<Community>>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<List<Community>>>() {
            });
            return responseEntity.getBody();
        });
        return resp.getResult();
    }

    /**
     * 添加房产
     * @param house
     */
    public void addHouse(House house) {
        Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/add");
            ResponseEntity<RestResponse<Object>> responseEntity = rest.post(url, house, new ParameterizedTypeReference<RestResponse<Object>>() {
            });
            return responseEntity.getBody();
        });
    }

    /**
     * 更新评分
     * @param id
     * @param rating
     */
    public void rating(Long id, Double rating) {
        Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/rating?id=" + id + "&rating=" + rating);
            ResponseEntity<RestResponse<Object>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<Object>>() {
            });
            return responseEntity.getBody();
        });
    }

    /**
     * 添加留言
     * @param userMsg
     */
    public void addUserMsg(UserMsg userMsg) {
        Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/addUserMsg");
            ResponseEntity<RestResponse<Object>> responseEntity = rest.post(url, userMsg, new ParameterizedTypeReference<RestResponse<Object>>() {
            });
            return responseEntity.getBody();
        });
    }

    /**
     * 获取最新房产
     * @return
     */
    public List<House> getLastest() {
        RestResponse<List<House>> resp = Rests.exc(() -> {

            String url = Rests.toUrl(houseServiceName, "/house/lastest");
            ResponseEntity<RestResponse<List<House>>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<List<House>>>() {
            });
            return responseEntity.getBody();
        });
        return resp.getResult();
    }

    /**
     * 获取房产列表
     *
     * @param query
     * @param limit
     * @param offset
     * @return
     */
    public ListResponse<House> getHouses(House query, Integer limit, Integer offset) {
        RestResponse<ListResponse<House>> resp = Rests.exc(() -> {
            HouseQueryReq req = new HouseQueryReq();
            req.setLimit(limit);
            req.setOffset(offset);
            req.setQuery(query);
            String url = Rests.toUrl(houseServiceName, "/house/list");
            ResponseEntity<RestResponse<ListResponse<House>>> responseEntity = rest.post(url, req, new ParameterizedTypeReference<RestResponse<ListResponse<House>>>() {
            });
            return responseEntity.getBody();
        });
        return resp.getResult();
    }

    /**
     * 获取热门房产
     * @param recomSize
     * @return
     */
    public List<House> getHotHouse(Integer recomSize) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/hot" + "?size=" + recomSize);
            ResponseEntity<RestResponse<List<House>>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<List<House>>>() {
            });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 获取房产详情
     * @param id
     * @return
     */
    public House getOneHouse(long id) {
        return Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/detail?id=" + id);
            ResponseEntity<RestResponse<House>> responseEntity = rest.get(url, new ParameterizedTypeReference<RestResponse<House>>() {
            });
            return responseEntity.getBody();
        }).getResult();
    }

    /**
     * 绑定(设置unbind字段为false)
     * @param houseId
     * @param userId
     * @param bookmark
     */
    public void bindUser2House(Long houseId, Long userId, boolean bookmark) {
        HouseUserReq req = new HouseUserReq();
        req.setUnBind(false);
        req.setBindType(HouseUserType.BOOKMARK.value);
        req.setUserId(userId);
        req.setHouseId(houseId);
        bindOrInBind(req);
    }

    /**
     * 绑定或者解绑
     * @param req
     */
    private void bindOrInBind(HouseUserReq req) {
        Rests.exc(() -> {
            String url = Rests.toUrl(houseServiceName, "/house/bind");
            ResponseEntity<RestResponse<Object>> responseEntity = rest.post(url, req, new ParameterizedTypeReference<RestResponse<Object>>() {
            });
            return responseEntity.getBody();
        });
    }

    /**
     * 解绑(设置unbind字段为true)
     * @param houseId
     * @param userId
     * @param book
     */
    public void unbindUser2House(Long houseId, Long userId, boolean book) {
        HouseUserReq req = new HouseUserReq();
        req.setUnBind(true);
        if (book) {
            req.setBindType(HouseUserType.BOOKMARK.value);
        } else {
            req.setBindType(HouseUserType.SALE.value);
        }
        req.setUserId(userId);
        req.setHouseId(houseId);
        bindOrInBind(req);
    }


}
