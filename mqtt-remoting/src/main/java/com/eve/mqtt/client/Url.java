package com.eve.mqtt.client;

/**
 * 类说明
 * <p>
 *
 * @author xieyang
 * @version 1.0.0
 * @date 2019/9/6
 */
public class Url {

    private String hostIp;

    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}
