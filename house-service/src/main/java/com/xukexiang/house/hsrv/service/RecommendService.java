package com.xukexiang.house.hsrv.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.xukexiang.house.hsrv.common.LimitOffset;
import com.xukexiang.house.hsrv.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecommendService {

    private static final String HOT_HOUSE_KEY = "_hot_house";

    @Autowired
    private HouseService houseService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public List<House> getHotHouse(Integer size) {
        if (size == null) {
            size = 3;
        }
        Set<String> idSet = redisTemplate.opsForZSet().reverseRange(HOT_HOUSE_KEY, 0, size - 1);//bug修复，根据热度从高到底排序
        //取出zset中保存的所有房产id
        List<Long> ids = idSet.stream().map(b -> Long.parseLong(b)).collect(Collectors.toList());
        House query = new House();
        query.setIds(ids);
        return houseService.queryAndSetImg(query, LimitOffset.build(size, 0));
    }

    /**
     * 根据id增长热度
     *
     * @param id
     */
    public void increaseHot(long id) {
        redisTemplate.opsForZSet().incrementScore(HOT_HOUSE_KEY, "" + id, 1.0D);
        redisTemplate.opsForZSet().removeRange(HOT_HOUSE_KEY, 0, -11);
    }

    /**
     * 获取最新的房产
     *
     * @return
     */
    public List<House> getLastest() {
        House query = new House();
        query.setSort("create_time");
        return houseService.queryAndSetImg(query, LimitOffset.build(8, 0));
    }


}
