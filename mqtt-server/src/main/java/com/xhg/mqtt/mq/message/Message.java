package com.xhg.mqtt.mq.message;


import com.xhg.mqtt.mq.POINT;
import com.xhg.mqtt.mq.proto.MqttMessagePb;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage;
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

    /**
     * payload解码后的消息
     */
    private MqttMessagePb.MqttMessage mqttMessage;

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

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }
}
