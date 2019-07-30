package com.xukexiang.house.hsrv.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.Arrays;

public class FastJsonHttpMessageConverter5 extends FastJsonHttpMessageConverter {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public FastJsonHttpMessageConverter5() {
        setDefaultCharset(DEFAULT_CHARSET);
        setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+")));

    }
}
