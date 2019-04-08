package com.xhg.mqtt.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by xieyang on 19/3/31.
 * @author xieyang
 */
public class MqttNettyClient extends AbstractMessageClient {



    private final static ExecutorService service = Executors.newFixedThreadPool(1);

    public MqttNettyClient(Bootstrap bootstrap,ClientOptions options, String clientId) {
        super(bootstrap,options, clientId);
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