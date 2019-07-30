package com.xukexiang.house.user.common;

import com.xukexiang.house.user.exception.IllegalParamsException.Type;
import com.xukexiang.house.user.exception.WithTypeException;

public class UserException extends RuntimeException implements WithTypeException {
    private static final long serialVersioUID = 1L;
    private Type type;

    public UserException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public enum Type {
        USER_NOT_LOGIN, USER_NOT_FOUND, USER_AUTH_FAIL;
    }
}
