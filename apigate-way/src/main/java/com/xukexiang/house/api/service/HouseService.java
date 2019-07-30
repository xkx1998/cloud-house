package com.xukexiang.house.api.service;

import java.util.List;

import com.xukexiang.house.api.common.ListResponse;
import com.xukexiang.house.api.common.PageData;
import com.xukexiang.house.api.common.PageParams;
import com.xukexiang.house.api.dao.HouseDao;
import com.xukexiang.house.api.model.*;
import com.xukexiang.house.api.utils.BeanHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Joiner;

@Service
public class HouseService {


    @Autowired
    private FileService fileService;

    @Autowired
    private HouseDao houseDao;


    /**
     * 更新评分
     * @param id
     * @param rating
     */
    public void updateRating(Long id, Double rating) {
        houseDao.rating(id, rating);
    }

    /**
     * 添加房产
     * @param house
     * @param user
     */
    public void addHouse(House house, User user) {
        if (house.getHouseFiles() != null && !house.getHouseFiles().isEmpty()) {
            List<MultipartFile> files = house.getHouseFiles();
            String imags = Joiner.on(",").join(fileService.getImgPaths(files));
            house.setImages(imags);
        }
        if (house.getFloorPlanFiles() != null && !house.getFloorPlanFiles().isEmpty()) {
            List<MultipartFile> files = house.getFloorPlanFiles();
            String floorPlans = Joiner.on(",").join(fileService.getImgPaths(files));
            house.setFloorPlan(floorPlans);
        }
        BeanHelper.setDefaultProp(house, House.class);
        BeanHelper.onInsert(house);
        house.setUserId(user.getId());
        houseDao.addHouse(house);
    }


    /**
     * 获取所有小区
     * @return
     */
    public List<Community> getAllCommunitys() {
        return houseDao.getAllCommunitys();
    }


    /**
     * 获取所有城市
     * @return
     */
    public List<City> getAllCitys() {
        return houseDao.getAllCitys();
    }


    /**
     * 添加留言
     * @param userMsg
     */
    public void addUserMsg(UserMsg userMsg) {
        houseDao.addUserMsg(userMsg);
    }


    /**
     * 获取最新房产
     * @return
     */
    public List<House> getLastest() {
        return houseDao.getLastest();
    }

    /**
     * 获取房产列表(查询)
     *
     * @param query
     * @param build
     * @return
     */
    public PageData<House> queryHouse(House query, PageParams build) {
        ListResponse<House> result = houseDao.getHouses(query, build.getLimit(), build.getOffset());
        return PageData.<House>buildPage(result.getList(), result.getCount(), build.getPageSize(), build.getPageNum());
    }

    /**
     * 获取热门房产
     *
     * @param recomSize
     * @return
     */
    public List<House> getHotHouse(Integer recomSize) {
        List<House> list = houseDao.getHotHouse(recomSize);
        return list;
    }

    /**
     * 获取房产详情
     *
     * @param id
     * @return
     */
    public House queryOneHouse(long id) {
        return houseDao.getOneHouse(id);
    }

    /**
     * 绑定用户和房产
     *
     * @param houseId
     * @param userId
     * @param bookmark
     */
    public void bindUser2House(Long houseId, Long userId, boolean bookmark) {
        houseDao.bindUser2House(houseId, userId, bookmark);
    }

    /**
     * 解绑用户和房产
     *
     * @param houseId
     * @param userId
     * @param b
     */
    public void unbindUser2House(Long houseId, Long userId, boolean b) {
        houseDao.unbindUser2House(houseId, userId, b);
    }


}
