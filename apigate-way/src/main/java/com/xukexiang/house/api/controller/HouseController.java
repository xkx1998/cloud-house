package com.xukexiang.house.api.controller;

import com.google.common.base.Objects;
import com.xukexiang.house.api.common.*;
import com.xukexiang.house.api.model.Comment;
import com.xukexiang.house.api.model.House;
import com.xukexiang.house.api.model.User;
import com.xukexiang.house.api.model.UserMsg;
import com.xukexiang.house.api.service.AgencyService;
import com.xukexiang.house.api.service.CommentService;
import com.xukexiang.house.api.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HouseController {
    @Autowired
    private HouseService houseService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private CommentService commentService;

    /**
     * 获取房产列表（查询）
     *
     * @param pageSize
     * @param pageNum
     * @param query
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "house/list", method = {RequestMethod.POST, RequestMethod.GET})
    public String houseList(Integer pageSize, Integer pageNum, House query, ModelMap modelMap) {
        PageData<House> ps = houseService.queryHouse(query, PageParams.build(pageSize, pageNum));
        //获取热门推荐房产
        List<House> rcHouses = houseService.getHotHouse(CommonConstants.RECOM_SIZE);
        modelMap.put("recomHouses", rcHouses);
        modelMap.put("vo", query);
        modelMap.put("ps", ps);
        return "/house/listing";
    }

    /**
     * 获取房产详情
     * 获取评论
     * 获取热门房产
     * 获取经纪人详情
     * @param id
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "house/detail", method = {RequestMethod.POST, RequestMethod.GET})
    public String houseDetail(long id, ModelMap modelMap) {
        //获取房产
        House house = houseService.queryOneHouse(id);
        //获取评论
        List<Comment> comments = commentService.getHouseComments(id);
        //获取热门房产
        List<House> rcHouses = houseService.getHotHouse(CommonConstants.RECOM_SIZE);
        if (house.getUserId() != null) {
            if (!Objects.equal(0L, house.getUserId())) {
                //获取经纪人详情
                modelMap.put("agent", agencyService.getAgentDetail(house.getUserId()));
            }
        }
        modelMap.put("house", house);
        modelMap.put("recomHouses", rcHouses);
        modelMap.put("commentList", comments);
        return "/house/detail";
    }

    /**
     * 给经纪人留言
     * @param userMsg
     * @return
     */
    @RequestMapping(value = "house/leaveMsg", method = {RequestMethod.POST, RequestMethod.GET})
    public String houseMsg(UserMsg userMsg) {
        houseService.addUserMsg(userMsg);
        return "redirect:/house/detail?id=" + userMsg.getHouseId() + "&" + ResultMsg.successMsg("留言成功").asUrlParams();
    }

    /**
     * 评分
     * @param rating
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "house/rating", method = {RequestMethod.POST, RequestMethod.GET})
    public ResultMsg houseRate(Double rating, Long id) {
        houseService.updateRating(id, rating);
        return ResultMsg.success();
    }

    /**
     * 房产添加页面
     * 显示citys
     * 显示communitys
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "house/toAdd", method = {RequestMethod.POST, RequestMethod.GET})
    public String toAdd(ModelMap modelMap) {
        modelMap.put("citys", houseService.getAllCitys());
        modelMap.put("communitys", houseService.getAllCommunitys());
        return "/house/add";
    }

    /**
     * 添加房产
     * @param house
     * @return
     */
    @RequestMapping(value = "house/add", method = {RequestMethod.POST, RequestMethod.GET})
    public String doAdd(House house) {
        User user = UserContext.getUser();
        houseService.addHouse(house, user);
        return "redirect:/house/ownlist?" + ResultMsg.successMsg("添加成功").asUrlParams();
    }

    /**
     * 个人房产列表
     * @param house
     * @param pageParams
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "house/ownlist", method = {RequestMethod.POST, RequestMethod.GET})
    public String ownlist(House house, PageParams pageParams, ModelMap modelMap) {
        User user = UserContext.getUser();
        house.setUserId(user.getId());
        house.setBookmarked(false);
        modelMap.put("ps", houseService.queryHouse(house, pageParams));
        modelMap.put("pageType", "own");
        return "/house/ownlist";
    }

    /**
     * 删除房产
     * @param id
     * @param pageType
     * @return
     */
    @RequestMapping(value = "house/del", method = {RequestMethod.POST, RequestMethod.GET})
    public String delsale(Long id, String pageType) {
        User user = UserContext.getUser();
        houseService.unbindUser2House(id, user.getId(), pageType.equals("own") ? false : true);
        return "redirect:/house/ownlist";
    }


    /**
     * 个人收藏房产列表
     * @param house
     * @param pageParams
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "house/bookmarked", method = {RequestMethod.POST, RequestMethod.GET})
    public String bookmarked(House house, PageParams pageParams, ModelMap modelMap) {
        User user = UserContext.getUser();
        house.setBookmarked(true);
        house.setUserId(user.getId());
        modelMap.put("ps", houseService.queryHouse(house, pageParams));
        modelMap.put("pageType", "book");
        return "/house/ownlist";
    }


    /**
     * 房屋收藏
     * @param id
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "house/bookmark", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultMsg bookmark(Long id, ModelMap modelMap) {
        User user = UserContext.getUser();
        houseService.bindUser2House(id, user.getId(), true);
        return ResultMsg.success();
    }

    /**
     * 房屋解绑
     * @param id
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "house/unbookmark", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultMsg unbookmark(Long id, ModelMap modelMap) {
        User user = UserContext.getUser();
        houseService.unbindUser2House(id, user.getId(), true);
        return ResultMsg.success();
    }
}
