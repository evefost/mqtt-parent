package com.eve.mqtt.mq.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @Name: MQTT配置文件
 * @Description: TODO
 * @Copyright: Copyright (c) 2018   
 * @Author chenxiaojun  
 * @Create Date 2018年7月16日  
 * @Version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "spring.mqtt")
@Data
public class MqttConfig {



    private String clientIdPrefix;


    /**
     *  broker 集群地址
     */
    private String[] nodes;

	/**
	 * 客户端id
	 */
	private String clientId;
	/**
	 * 订阅topic
	 */
	private String[] topics;
	/**
	 * 用户名
	 */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 超时时间
     */
    private String timeout;
    /**
     * 心跳时间间隔
     */
    private int keepalive;
    /**
     * 遗嘱topic
     */
    private String willTopic;
    /**
     * 产品关键字
     */
    private String productKey;
    /**
     * 是否启动重试
     */
    private Boolean	retryEnable;
    /**
     * 充值间隔（单位秒）		
     */
    private int retryInterval = 60;
    /**
     * 重试次数
     */
    private int retryTimes = 3;
    /**
     * 应答扫描有效时间,在消息发送后n分钟内扫描回复（分钟）
     */
    private int ackScanVaild = 6;
    /**
     * 设备登陆后间隔开始重发间隔时间(分钟)
     */
    private int retryAfterLoginInterval = 3;
    /**
     * 是否系统自动ack设备指令
     */
    private Boolean serverAutoAck;
    /**
     * 异常通知
     */
    private String serverNoticeMails;
    /**
     * 用于处理消息的线程数
     */
    private int threadCount = 10;

    /**
     * 消息费端连接数
     */
    private int consumerCount=10;

    /**
     * 生产端连接数
     */
    private int producerCount=10;

    /**
     * mqtt发送间隔时间（s）
     */
    private int sendControllTime = 5;
    /**
     * 指令发送细化级别
     */
    private int refineLevel = 2;

    public String getClientIdPrefix() {
        return clientIdPrefix;
    }

    public void setClientIdPrefix(String clientIdPrefix) {
        this.clientIdPrefix = clientIdPrefix;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public int getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(int keepalive) {
        this.keepalive = keepalive;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public Boolean getRetryEnable() {
        return retryEnable;
    }

    public void setRetryEnable(Boolean retryEnable) {
        this.retryEnable = retryEnable;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getAckScanVaild() {
        return ackScanVaild;
    }

    public void setAckScanVaild(int ackScanVaild) {
        this.ackScanVaild = ackScanVaild;
    }

    public int getRetryAfterLoginInterval() {
        return retryAfterLoginInterval;
    }

    public void setRetryAfterLoginInterval(int retryAfterLoginInterval) {
        this.retryAfterLoginInterval = retryAfterLoginInterval;
    }

    public Boolean getServerAutoAck() {
        return serverAutoAck;
    }

    public void setServerAutoAck(Boolean serverAutoAck) {
        this.serverAutoAck = serverAutoAck;
    }

    public String getServerNoticeMails() {
        return serverNoticeMails;
    }

    public void setServerNoticeMails(String serverNoticeMails) {
        this.serverNoticeMails = serverNoticeMails;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getConsumerCount() {
        return consumerCount;
    }

    public void setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
    }

    public int getProducerCount() {
        return producerCount;
    }

    public void setProducerCount(int producerCount) {
        this.producerCount = producerCount;
    }

    public int getSendControllTime() {
        return sendControllTime;
    }

    public void setSendControllTime(int sendControllTime) {
        this.sendControllTime = sendControllTime;
    }

    public int getRefineLevel() {
        return refineLevel;
    }

    public void setRefineLevel(int refineLevel) {
        this.refineLevel = refineLevel;
    }
}

