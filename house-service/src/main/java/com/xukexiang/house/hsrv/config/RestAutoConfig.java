package com.xukexiang.house.hsrv.config;

import org.apache.http.client.HttpClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Configuration
public class RestAutoConfig {
    public static class RestTemplateConfig {

        @Bean
        @LoadBalanced  //spring对restTemplate bean进行替换，加入loadBalance拦截器ip:port的替换
        RestTemplate lbRestTemplate(HttpClient httpClient) {
            RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
            template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("utf-8")));
            template.getMessageConverters().add(1, new FastJsonHttpMessageConverter5());
            return template;
        }

        @Bean
        RestTemplate directRestTemplate(HttpClient httpClient) {
            RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
            template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("utf-8")));
            template.getMessageConverters().add(1, new FastJsonHttpMessageConverter5());
            return template;
        }



    }
}
