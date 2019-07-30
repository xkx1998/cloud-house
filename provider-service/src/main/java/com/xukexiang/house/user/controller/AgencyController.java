package com.xukexiang.house.user.controller;

import com.xukexiang.house.user.common.RestResponse;
import com.xukexiang.house.user.model.Agency;
import com.xukexiang.house.user.common.ListResponse;
import com.xukexiang.house.user.common.PageParams;
import com.xukexiang.house.user.model.User;
import com.xukexiang.house.user.service.AgencyService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("agency")
public class AgencyController {
    @Autowired
    private AgencyService agencyService;

    /**
     * 经纪人列表
     *
     * @param limit
     * @param offset
     * @return
     */
    @RequestMapping("agentList")
    public RestResponse<ListResponse<User>> agentList(Integer limit, Integer offset) {
        PageParams pageParams = new PageParams();
        pageParams.setLimit(limit);
        pageParams.setOffset(offset);
        Pair<List<User>, Long> pair = agencyService.getAllAgent(pageParams);
        ListResponse<User> response = ListResponse.build(pair.getKey(), pair.getValue());
        return RestResponse.success(response);
    }

    /**
     * 经纪人详情
     *
     * @param id
     * @return
     */
    @RequestMapping("agentDetail")
    public RestResponse<User> agentDetail(Long id) {
        User user = agencyService.getAgentDetail(id);
        return RestResponse.success(user);
    }

    /**
     * 添加经济机构
     *
     * @param agency
     * @return
     */
    @RequestMapping("add")
    public RestResponse<Object> addAgency(@RequestBody Agency agency) {
        agencyService.add(agency);
        return RestResponse.success();
    }

    /**
     * 经纪机构列表
     *
     * @return
     */
    @RequestMapping("list")
    public RestResponse<List<Agency>> agencyList() {
        List<Agency> agencies = agencyService.getAllAgency();
        return RestResponse.success(agencies);
    }

    /**
     * 经济机构详情
     *
     * @param id
     * @return
     */
    @RequestMapping("agencyDetail")
    public RestResponse<Agency> agencyDetail(Integer id) {
        Agency agency = agencyService.getAgency(id);
        return RestResponse.success(agency);
    }


}
