package com.tech.mediaserver.entity;

public class RespBean {
    private Integer status;
    private String msg;
    private Object result;

    private RespBean() {
    }

    public static RespBean build() {
        return new RespBean();
    }

    public static RespBean ok(String msg, Object result) {
        return new RespBean(200, msg, result);
    }

    public static RespBean ok(String msg) {
        return new RespBean(200, msg, null);
    }

    public static RespBean error(String msg, Object result) {
        return new RespBean(500, msg, result);
    }

    public static RespBean error(String msg) {
        return new RespBean(500, msg, null);
    }

    private RespBean(Integer status, String msg, Object result) {
        this.status = status;
        this.msg = msg;
        this.result = result;
    }

    public Integer getStatus() {

        return status;
    }

    public RespBean setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RespBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public RespBean setResult(Object result) {
        this.result = result;
        return this;
    }
}
