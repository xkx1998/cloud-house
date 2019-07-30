package com.xukexiang.house.user.common;

public class RestResponse<T> {
    private int code;
    private String msg;
    private T result;

    public static <T> RestResponse<T> success() {
        RestResponse<T> response = new RestResponse<>();
        return response;
    }

    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        return response;
    }

    public static <T> RestResponse<T> error(RestCode code) {
        return new RestResponse<>(code.code, code.msg);
    }

    public RestResponse() {
        this(RestCode.OK.code, RestCode.OK.msg);
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
