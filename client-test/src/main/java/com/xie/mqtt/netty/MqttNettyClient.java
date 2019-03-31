package com.xie.mqtt.netty;

import io.netty.channel.Channel;

/**
 * created by xieyang on 19/3/31.
 * @author xieyang
 */
public class MqttNettyClient extends AbstractMessageClient {

    public MqttNettyClient(String clientId, Channel channel, String... topics){
        this.clientId = clientId;
        this.channel = channel;
        this.topics=topics;
    }


}