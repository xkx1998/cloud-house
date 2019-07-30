package com.xukexiang.house.hsrv.mapper;

import java.util.List;

import com.xukexiang.house.hsrv.model.City;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CityMapper {
  
  public List<City> selectCitys(City city);

}
