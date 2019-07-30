package com.xukexiang.house.hsrv.common;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GenericRest {
  
  @Autowired
  private RestTemplate lbRestTemplate;
  
  @Autowired
  private RestTemplate directRestTemplate;
  
  private static final  String directFlag = "direct://";
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  public  <T> ResponseEntity<T> post(String url,Object reqBody,ParameterizedTypeReference<T> responseType){
    Pair<RestTemplate, String> pair = getRestTemplate(url);
    return pair.getLeft().exchange(pair.getRight(),HttpMethod.POST, new HttpEntity(reqBody),  responseType);
  }

  private  Pair<RestTemplate, String> getRestTemplate(String url) {
    RestTemplate rest = lbRestTemplate;
    if (url.contains(directFlag)) {
      rest = directRestTemplate;
      url = url.replace("direct://", "");
    }
    return Pair.of(rest, url);
  }
  
  public  <T> ResponseEntity<T> get(String url,ParameterizedTypeReference<T> responseType){
    Pair<RestTemplate, String> pair  = getRestTemplate(url);
    return pair.getLeft().exchange(pair.getRight(),HttpMethod.GET, HttpEntity.EMPTY,responseType);
  }

  public RestTemplate getPlainRest(){
    return directRestTemplate;
  }
  
  public RestTemplate getLbRest(){
    return lbRestTemplate;
  }
}
