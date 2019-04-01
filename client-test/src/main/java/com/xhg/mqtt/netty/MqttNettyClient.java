package com.xhg.mqtt.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by xieyang on 19/3/31.
 * @author xieyang
 */
public class MqttNettyClient extends AbstractMessageClient {


    private final static ExecutorService service = Executors.newFixedThreadPool(20);


    public MqttNettyClient(Bootstrap bootstrap,ClientOptions options, String clientId, Channel channel) {
        super(bootstrap,options, clientId, channel);
    }



    @Override
    public void onReceived(String topic,MqttPublishMessage msg) {

    }


    @Override
    public void onClosed(Throwable cause) {
        if (options.isAutoReconnect()) {
            if (!channel.isActive()) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        reconnect(false);
                    }
                });
            }
        }
    }




}