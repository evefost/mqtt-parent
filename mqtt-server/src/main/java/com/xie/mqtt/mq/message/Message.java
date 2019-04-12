package com.xie.mqtt.mq.message;


import com.xie.mqtt.common.POINT;
import com.xie.mqtt.common.proto.MqttMessagePb;
import com.xie.mqtt.common.proto.MqttMessagePb.MqttMessage;
import lombok.Data;

@Data
public class Message<S> {

    /**
     * 消息来自那一端
     */
    private POINT from;

    /**
     * 消息发往那一端
     */
    private POINT to;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 源消息体
     */
    private S srcMessage;

    /**
     * payload 原始数据
     */
    private byte[] mqttPayload;

    private String clientId;

    /**
     * payload解码后的消息
     */
    private MqttMessagePb.MqttMessage buzMessage;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public POINT getFrom() {
        return from;
    }

    public void setFrom(POINT from) {
        this.from = from;
    }

    public POINT getTo() {
        return to;
    }

    public void setTo(POINT to) {
        this.to = to;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public S getSrcMessage() {
        return srcMessage;
    }

    public void setSrcMessage(S srcMessage) {
        this.srcMessage = srcMessage;
    }

    public byte[] getMqttPayload() {
        return mqttPayload;
    }

    public void setMqttPayload(byte[] mqttPayload) {
        this.mqttPayload = mqttPayload;
    }

    public MqttMessage getBuzMessage() {
        return buzMessage;
    }

    public void setBuzMessage(MqttMessage buzMessage) {
        this.buzMessage = buzMessage;
    }
}
