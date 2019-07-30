package com.xukexiang.house.hsrv.controller;

import java.util.List;

import com.xukexiang.house.hsrv.common.*;
import com.xukexiang.house.hsrv.model.*;
import com.xukexiang.house.hsrv.service.HouseService;
import com.xukexiang.house.hsrv.service.RecommendService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Objects;

@RequestMapping("house")
@RestController
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private RecommendService recommendService;

    /**
     * 房产查询
     *
     * @param req
     * @return
     */
    @RequestMapping("list")
    public RestResponse<ListResponse<House>> houseList(@RequestBody HouseQueryReq req) {
        Integer limit = req.getLimit();
        Integer offset = req.getOffset();
        House query = req.getQuery();
        /**两个返回值：List<House>(房产查询结果列表) ，Long(查询总数) */
        Pair<List<House>, Long> pair = houseService.queryHouse(query, LimitOffset.build(limit, offset));
        return RestResponse.success(ListResponse.build(pair.getKey(), pair.getValue()));
    }

    /**
     * 房产详情
     *
     * @param id
     * @return
     */
    @RequestMapping("detail")
    public RestResponse<House> houseDetail(long id) {
        House house = houseService.queryOneHouse(id);
        /**该房产点击量加一*/
        recommendService.increaseHot(id);
        return RestResponse.success(house);
    }

    /**
     * 向经纪人添加留言
     *
     * @param userMsg
     * @return
     */
    @RequestMapping("addUserMsg")
    public RestResponse<Object> houseMsg(@RequestBody UserMsg userMsg) {
        houseService.addUserMsg(userMsg);
        return RestResponse.success();
    }

    /**
     * 房产评分
     *
     * @param rating
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("rating")
    public RestResponse<Object> houseRate(Double rating, Long id) {
        houseService.updateRating(id, rating);
        return RestResponse.success();
    }

//
//  @RequestMapping("allCommunitys")
//  public RestResponse<List<Community>> toAdd(){
//    List<Community> list = houseService.getAllCommunitys();
//    return RestResponse.success(list);
//  }
//
//  @RequestMapping("allCitys")
//  public RestResponse<List<City>> allCitys(){
//    List<City> list = houseService.getAllCitys();
//    return RestResponse.success(list);
//  }
//

    /**
     * 房产新增
     *
     * @param house
     * @return
     */
    @RequestMapping("add")
    public RestResponse<Object> doAdd(@RequestBody House house) {
        house.setState(CommonConstants.HOUSE_STATE_UP);
        if (house.getUserId() == null) {
            return RestResponse.error(RestCode.ILLEGAL_PARAMS);
        }
        houseService.addHouse(house, house.getUserId());
        return RestResponse.success();
    }

    /**
     * 房产收藏和解绑
     *
     * @param req
     * @return
     */
    @RequestMapping("bind")
    public RestResponse<Object> delsale(@RequestBody HouseUserReq req) {
        Integer bindType = req.getBindType();
        HouseUserType houseUserType = Objects.equal(bindType, 1) ? HouseUserType.SALE : HouseUserType.BOOKMARK;
        /**判断req是绑定还是解绑*/
        if (req.isUnBind()) {
            houseService.unbindUser2Houser(req.getHouseId(), req.getUserId(), houseUserType);
        } else {
            houseService.bindUser2House(req.getHouseId(), req.getUserId(), houseUserType);
        }
        return RestResponse.success();
    }

    /**
     * 热门房产推荐
     *
     * @param size
     * @return
     */
    @RequestMapping("hot")
    public RestResponse<List<House>> getHotHouse(Integer size) {
        List<House> list = recommendService.getHotHouse(size);
        return RestResponse.success(list);
    }

    /**
     * 最新房产
     *
     * @return
     */
    @RequestMapping("lastest")
    public RestResponse<List<House>> getLastest() {
        return RestResponse.success(recommendService.getLastest());
    }

}
