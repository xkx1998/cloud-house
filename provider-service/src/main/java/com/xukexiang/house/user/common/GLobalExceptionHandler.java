package com.xukexiang.house.user.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常的增强
 */
@ControllerAdvice
public class GLobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GLobalExceptionHandler.class);

    /**
     * 异常处理器
     * @param req
     * @param throwable
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public RestResponse<Object> handler(HttpServletRequest req, Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);
        Object target = throwable;
        // 返回自定义的异常实例
        RestCode restCode = Exception2CodeRepo.getCode(throwable);
        RestResponse<Object> response = new RestResponse<Object>(restCode.code, restCode.msg);
        return response;
    }
}
