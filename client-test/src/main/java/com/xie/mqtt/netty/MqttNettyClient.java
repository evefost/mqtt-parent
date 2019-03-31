package com.xie.mqtt.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * created by xieyang on 19/3/31.
 * @author xieyang
 */
public class MqttNettyClient extends AbstractMessageClient {




    private ExecutorService service = Executors.newFixedThreadPool(20);



    public MqttNettyClient(Bootstrap bootstrap,ClientOptions options, String clientId, Channel channel) {
        super(bootstrap,options, clientId, channel);
    }



    @Override
    public void onReceived(MqttMessage msg) {
        if (options.isAutoReconnect()) {
            if (!channel.isActive()) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {

                        logger.info("[{}]重连=====>>>>", clientId);
                        reconnect();
                    }
                });
            }
        }

    }





}