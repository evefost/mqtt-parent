package com.xhg.mqtt.netty;

import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * Created by xieyang on 19/3/31.
 */
public interface MessageClient {

    String getClientId();

    void send(MqttMessage msg);

    void onReceived(MqttMessage msg);

    void onClosed(Throwable cause);


}
