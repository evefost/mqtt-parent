package com.xhg.mqtt.common;

/**
 * @Name: 事件代码
 * @Description: TODO
 * @Copyright: Copyright (c) 2018
 * @Author dengyunhui
 * @Create Date 2018年12月3日
 * @Version 1.0.0
 */
public enum EventCodeEnum {

    BOX_INFO("boxInfo", "箱子信息上报"),
    SITE_INFO("siteInfo", "站点信息上报"),
    VERSION_INFO("versionInfo", "版本信息上报"),
    SERVER_NOTIFY("serverNotify", "服务端主动通知"),
    DEVICE_LOGIN("deviceLogin", "上线状态上报"),
    DEVICE_LOGOUT("deviceLogout", "下线状态上报"),
    DEVICE_WILL("deviceWill", "异常下线上报"),
    SERVICE_DEVICE_ADD("addDevice", "添加设备"),
    SERVICE_DEVICE_REMOVE("removeDevice", "删除设备");

    /**
     * 编码
     */
    private String code;
    /**
     * 描述
     */
    private String desc;

    EventCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EventCodeEnum enumOf(String code) {
        for (EventCodeEnum eventCodeEnum : EventCodeEnum.values()) {
            if (eventCodeEnum.getCode().equals(code)) {
                return eventCodeEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
