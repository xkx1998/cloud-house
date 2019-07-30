package com.xukexiang.house.user.service;

import com.netflix.discovery.converters.Auto;
import com.xukexiang.house.user.common.PageParams;
import com.xukexiang.house.user.mapper.AgencyMapper;
import com.xukexiang.house.user.model.Agency;
import com.xukexiang.house.user.model.User;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgencyService {

    @Autowired
    private AgencyMapper agencyMapper;

    @Value("${images.prefix}")
    private String imgPrefix;

    /**
     * 获取所有经纪人
     *
     * @param pageParams
     * @return
     */
    public Pair<List<User>, Long> getAllAgent(PageParams pageParams) {
        List<User> agents = agencyMapper.selectAgent(new User(), pageParams);
        setImg(agents);
        Long count = agencyMapper.selectAgentCount(new User());
        return ImmutablePair.of(agents, count);
    }

    /**
     * 还原图片路径
     *
     * @param users
     */
    public void setImg(List<User> users) {
        users.forEach(u -> {
            u.setAvatar(imgPrefix + u.getAvatar());
        });
    }

    /**
     * 根据id获取经纪人详情
     *
     * @param id
     * @return
     */
    public User getAgentDetail(Long id) {
        User user = new User();
        user.setId(id);
        user.setType(2);
        List<User> list = agencyMapper.selectAgent(user, new PageParams(1, 1));
        setImg(list);
        if (!list.isEmpty()) {
            User agent = list.get(0);
            //将经纪人关联的经纪机构也一并查询出来
            Agency agency = new Agency();
            agency.setId(agent.getAgencyId().intValue());
            List<Agency> agencies = agencyMapper.select(agency);
            if (!agencies.isEmpty()) {
                agent.setAgencyName(agencies.get(0).getName());
            }
            return agent;
        }
        return null;
    }

    /**
     * 获取所有经纪机构
     *
     * @return
     */
    public List<Agency> getAllAgency() {
        return agencyMapper.select(new Agency());
    }

    /**
     * 根据id获取经纪机构
     *
     * @param id
     * @return
     */
    public Agency getAgency(Integer id) {
        Agency agency = new Agency();
        agency.setId(id);
        List<Agency> agencies = agencyMapper.select(agency);
        if (agencies.isEmpty()) {
            return null;
        }
        return agencies.get(0);
    }

    /**
     * 添加经纪机构
     *
     * @param agency
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int add(Agency agency) {
        return agencyMapper.insert(agency);
    }
}
