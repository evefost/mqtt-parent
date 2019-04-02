package com.xhg.mqtt.netty;

import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * Created by xieyang on 19/3/31.
 */
public interface MessageClient {

    String getClientId();

    void send(MqttMessage msg);

    void onReceived(String topic,MqttPublishMessage msg);

    void onClosed(Throwable cause);


    ClientOptions getOptions();

    void disconnect();

    void reconnect(boolean immediately);
}