package com.xie.mqtt.controller;

import com.sun.javafx.UnmodifiableArrayList;
import com.xie.mqtt.netty.ClientOptions;
import com.xie.mqtt.netty.MessageClient;
import com.xie.mqtt.netty.MessageClientFactory;
import com.xie.mqtt.netty.SingletonClient;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by xieyang on 19/3/30.
 */
@RestController
@RequestMapping("client")
public class MqttClientController implements InitializingBean {


    private static final Logger logger = LoggerFactory.getLogger(MqttClientController.class);

    private AtomicInteger channelCount = new AtomicInteger(0);
    private List<MessageClient> channels = new ArrayList<>();

    private  Random r = new Random();

    @GetMapping("/create/netty")
    String createNettyClient(int count) throws MqttException, InterruptedException {
        for (int i = 0; i < count; i++) {
            try {
                MessageClient channel = MessageClientFactory.getAndCreateChannel(options);
            } catch (InterruptedException e) {
               logger.error("连接通道失败");
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return channels.size() + "";
    }

    @GetMapping("/create")
    String createNettyClient() throws MqttException, InterruptedException, CloneNotSupportedException {
        SingletonClient instance = SingletonClient.getInstance(options);
        return instance.getClientId();
    }





    @GetMapping("/send")
    String send(String topic) throws MqttException {
        String topic2 = "/topic/client-"+topic;
        logger.debug("发布的topic:{}",topic2);
        UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getNettyChannels();

        MessageClient mqttClient = nettyChannels.get(r.nextInt(nettyChannels.size()));
        MqttPublishMessage publish = MqttMessageBuilders.publish()
                .topicName(topic2)
                .retained(false)
                .qos(MqttQoS.AT_MOST_ONCE)
                .payload(Unpooled.copiedBuffer("Hello MQTT world!".getBytes(UTF_8))).build();
        mqttClient.send(publish);
        return "send success";
    }

    @GetMapping("/broadcast")
    String broadcast() throws MqttException {
        String topic2 = "/topic/all";
        logger.debug("发布的topic:{}",topic2);
        UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getNettyChannels();

        MessageClient mqttClient = nettyChannels.get(r.nextInt(nettyChannels.size()));
        MqttPublishMessage publish = MqttMessageBuilders.publish()
                .topicName(topic2)
                .retained(false)
                .qos(MqttQoS.AT_MOST_ONCE)
                .payload(Unpooled.copiedBuffer("这是一条广播消息!".getBytes(UTF_8))).build();
        mqttClient.send(publish);
        return "send success";
    }

    ClientOptions options;
    @Override
    public void afterPropertiesSet() throws Exception {
        options = new ClientOptions();
        String[] nodes = {"127.0.0.1:1883"};
        options.setBrokerNodes(nodes);

        new Thread(){
            @Override
            public void run() {
               while (true){
                   try {
                       Thread.sleep(60000L);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getNettyChannels();
                   nettyChannels.forEach((c)->{
                       c.ping();
                   });
               }
            }
        }.start();
    }



}
