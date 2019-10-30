package com.eve.mqtt.pojo;

/**
 *
 * @author xieyang
 * @date 19/10/30
 */
public class Response<T> {

    private int code;

    private String message;

    private T data;


    public static <T> Response<T> success(T data){
        Response<T> response = new Response();
        response.setCode(200);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> failue(String message){
        Response<T> response = new Response();
        response.setCode(1);
        response.setMessage(message);
        return response;
    }


    public static <T> Response<T> sysError(String message){
        Response<T> response = new Response();
        response.setCode(500);
        response.setMessage(message);
        return response;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
