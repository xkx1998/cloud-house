package com.xukexiang.house.api.utils;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Rests {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rests.class);

    /**
     * 默认构造器
     */
    private Rests() {
    }

    private static DefaultHanlder defaultHanlder = new DefaultHanlder();


    /**
     * 执行服务调用，并判断返回状态
     *
     * @param callable
     * @return
     */
    public static <T> T exc(Callable<T> callable) {
        return exc(callable, defaultHanlder);
    }

    /**
     * 使用默认处理器、将UserDao中的一段执行代码封装成callable,调用call()获得返回值
     *
     * @param callable
     * @param handler
     * @param <T>
     * @return
     */
    public static <T> T exc(Callable<T> callable, ResultHandler handler) {
        //执行UserDao中的方法
        T result = sendReq(callable);
        //处理code
        return handler.handle(result);
    }

    /**
     * 根据serviceName 和Path生成Url调用服务
     *
     * @param serviceName
     * @param path
     * @return
     */
    public static String toUrl(String serviceName, String path) {
        return "http://" + serviceName + path;
    }

    public static class DefaultHanlder implements ResultHandler {

        /**
         * 处理返回值的方法
         * 根据code,处理异常
         * @param result
         * @param <T>
         * @return
         */
        @Override
        public <T> T handle(T result) {
            int code = 1;
            String msg = "";
            try {
                code = (Integer) FieldUtils.readDeclaredField(result, "code", true);
                msg = (String) FieldUtils.readDeclaredField(result, "msg", true);
            } catch (Exception e) {
                //ignore
            }
            if (code != 0) {
                throw new RestException("Get erroNo " + code + " when execute rest call with errorMsg " + msg);
            }
            return result;
        }

    }

    /**
     * 定义处理器接口
     */
    public interface ResultHandler {
        <T> T handle(T result);
    }

    /**
     * 执行方法并打印日志
     *
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> T sendReq(Callable<T> callable) {
        T result = null;
        try {
            //执行callable
            result = callable.call();
        } catch (Exception e) {
            throw new RestException("sendReq error by " + e.getMessage());
        } finally {
            LOGGER.info("result={}", result);
        }
        return result;
    }


}
