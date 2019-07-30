package com.xukexiang.house.user.mapper;

import com.xukexiang.house.user.common.PageParams;
import com.xukexiang.house.user.model.Agency;
import com.xukexiang.house.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgencyMapper {
    List<Agency> select(Agency agency);

    int insert(Agency agency);

    List<User> selectAgent(@Param("user") User user, @Param("pageParams") PageParams pageParams);

    Long selectAgentCount(@Param("user") User user);
}
