package com.xie.mqtt.netty;

import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * Created by xieyang on 19/3/31.
 */
public interface MessageClient {

    String getClientId();

    void subscript();

    void connect();

    void send(MqttMessage mqttMessage);


}
