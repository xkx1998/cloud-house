package com.xukexiang.house.hsrv.service;

import com.google.common.collect.Lists;
import com.xukexiang.house.hsrv.common.BeanHelper;
import com.xukexiang.house.hsrv.common.HouseUserType;
import com.xukexiang.house.hsrv.common.LimitOffset;
import com.xukexiang.house.hsrv.dao.UserDao;
import com.xukexiang.house.hsrv.mapper.HouseMapper;
import com.xukexiang.house.hsrv.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {
    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private MailService mailService;

    @Autowired
    UserDao userDao;

    @Value("${images.prefix}")
    private String imgPrefix;

    /**
     * 添加房产
     * 绑定房产到用户的关系
     *
     * @param house
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void addHouse(House house, Long userId) {
        BeanHelper.setDefaultProp(house, House.class);
        BeanHelper.onInsert(house);
        houseMapper.insert(house);
        bindUser2House(house.getId(), userId, HouseUserType.SALE);
    }

    /**
     * 绑定user和house
     *
     * @param houseId
     * @param userId
     * @param sale
     */
    public void bindUser2House(Long houseId, Long userId, HouseUserType sale) {
        HouseUser existHouseUser = houseMapper.selectHouseUser(userId, houseId, sale.value);
        if (existHouseUser != null) {
            return;
        }
        HouseUser houseUser = new HouseUser();
        houseUser.setHouseId(houseId);
        houseUser.setUserId(userId);
        houseUser.setType(sale.value);
        BeanHelper.setDefaultProp(houseUser, HouseUser.class);
        BeanHelper.onInsert(houseUser);
        houseMapper.insertHouseUser(houseUser);
    }

    /**
     * user和house解绑
     * 当房产售卖时只能下架，不能解绑用户关系
     * 当收藏时可以解除用户收藏房产这一关系
     *
     * @param houseId
     * @param userId
     * @param type
     */
    public void unbindUser2Houser(Long houseId, Long userId, HouseUserType type) {
        if (type.equals(HouseUserType.SALE)) {
            houseMapper.downHouse(houseId);
        } else {
            houseMapper.deleteHouseUser(houseId, userId, type.value);
        }
    }

    public List<House> queryAndSetImg(House query, LimitOffset pageParams) {
        List<House> houses = houseMapper.selectHouse(query, pageParams);
        houses.forEach(h -> {
            h.setFirstImg(imgPrefix + h.getFirstImg());
            h.setImageList(h.getImageList().stream().map(img -> imgPrefix + img).collect(Collectors.toList()));
            h.setFloorPlanList(h.getFloorPlanList().stream().map(img -> imgPrefix + img).collect(Collectors.toList()));
        });
        return houses;
    }

    /**
     * 房产查询
     *
     * @param query(查询条件)
     * @param build(分页)
     * @return
     */
    public Pair<List<House>, Long> queryHouse(House query, LimitOffset build) {
        List<House> houses = Lists.newArrayList();
        House houseQuery = query;
        if (StringUtils.isNoneBlank(query.getName())) {
            Community community = new Community();
            community.setName(query.getName());
            List<Community> communities = houseMapper.selectCommunity(community);
            if (!communities.isEmpty()) {
                houseQuery = new House();
                houseQuery.setCommunityId(communities.get(0).getId());
            }
        }
        houses = queryAndSetImg(houseQuery, build);
        Long count = houseMapper.selectHouseCount(houseQuery);
        return ImmutablePair.of(houses, count);
    }

    /**
     * 房产详情
     *
     * @param id(houseId)
     * @return
     */
    public House queryOneHouse(long id) {
        House query = new House();
        query.setId(id);
        List<House> houses = queryAndSetImg(query, LimitOffset.build(1, 0));
        if (!houses.isEmpty()) {
            return houses.get(0);
        }
        return null;
    }

    /**
     * 向经纪人发送留言通知
     *
     * @param userMsg
     */
    public void addUserMsg(UserMsg userMsg) {
        BeanHelper.onInsert(userMsg);
        BeanHelper.setDefaultProp(userMsg, UserMsg.class);
        houseMapper.insertUserMsg(userMsg);
        /**获取经纪人的邮箱地址*/
        User user = userDao.getAgentDetail(userMsg.getAgentId());
        mailService.sendSimpleMail("来自用户" + userMsg.getEmail(), userMsg.getMsg(), user.getEmail());

    }

    /**
     * 更新评分
     * 取平均值，最高为5分
     *
     * @param id
     * @param rating
     */
    public void updateRating(Long id, Double rating) {
        House house = queryOneHouse(id);
        Double oldRating = house.getRating();
        Double newRating = oldRating.equals(0D) ? rating : Math.min(Math.round(oldRating + rating) / 2, 5);
        House updateHouse = new House();
        updateHouse.setId(id);
        updateHouse.setRating(newRating);
        houseMapper.updateHouse(updateHouse);
    }


}
