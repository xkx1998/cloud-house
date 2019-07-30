package com.xukexiang.house.user.common;

import com.google.common.collect.ImmutableMap;
import com.xukexiang.house.user.exception.IllegalParamsException;
import com.xukexiang.house.user.exception.WithTypeException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.reflect.FieldUtils;

/**
 * 自定义异常 转化为 RestCode(code,msg)
 */
public class Exception2CodeRepo {
    private static final ImmutableMap<Object, RestCode> MAP = ImmutableMap.<Object, RestCode>builder()
            .put(IllegalParamsException.Type.WRONG_PAGE_NUM, RestCode.WRONG_PAGE)
            .put(UserException.Type.USER_NOT_LOGIN,RestCode.TOKEN_INVALID)
            .put(IllegalStateException.class, RestCode.UNKOWN_ERROR).build();

    private static Object getType(Throwable throwable) {
        try {
            return FieldUtils.readDeclaredField(throwable, "type", true);
        } catch (Exception e) {
            return null;
        }
    }

    public static RestCode getCode(Throwable throwable) {
        if (throwable == null) {
            return RestCode.UNKOWN_ERROR;
        }

        Object target = throwable;

        if (throwable instanceof WithTypeException) {
            Object type = getType(throwable);
            if (type != null) {
                target = type;
            }
        }
        RestCode restCode = MAP.get(target);
        if (restCode != null) {
            return restCode;
        }

        Throwable rootCause = ExceptionUtils.getRootCause(throwable);

        //递归调用打印出栈
        if (restCode != null) {
            return getCode(rootCause);
        }
        return RestCode.UNKOWN_ERROR;
    }
}
