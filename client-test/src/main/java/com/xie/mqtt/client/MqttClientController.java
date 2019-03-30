package com.xie.mqtt.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
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

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by xieyang on 19/3/30.
 */
@RestController
@RequestMapping("client")
public class MqttClientController implements InitializingBean {


    private static final Logger logger = LoggerFactory.getLogger(MqttClientController.class);


    List<MqttClient> clients = new ArrayList<MqttClient>();

    private AtomicInteger clientCount = new AtomicInteger(0);

    @GetMapping("/create")
    String createClient(int count) throws MqttException {


        for (int i = 0; i < count; i++) {

            MqttClient client = null;
            try {
                Thread.sleep(20L);
                client = new MqttClient("tcp://localhost:1883", "device-" + clientCount.incrementAndGet(), new MemoryPersistence());

                client.setCallback(new MessageCallback(client));
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                client.connect(options);
                String singleTopic = "/topic/"+clientCount.get();
                String bordcastTopic = "/topic/all";
                String[] topics = {singleTopic,bordcastTopic};
                client.subscribe(topics);
                clients.add(client);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return clients.size() + "";
    }

    private AtomicInteger channelCount = new AtomicInteger(0);
    private List<Channel> channels = new ArrayList<>();
    @GetMapping("/create/netty")
    String createNettyClient(int count) throws MqttException, InterruptedException {

        Client client = new Client("localhost");

        for (int i = 0; i < count; i++) {

            try {
                Channel channel = client.newChandler();
                channels.add(channel);
            } catch (InterruptedException e) {
               logger.error("连接通道失败");
            }
        }
        Thread.sleep(1000L);
        int id = 0;
        for(Channel channel:channels) {
            id++;
            String topic = "/topic/"+id;
            logger.debug("订阅的topic:{}",topic);
            MqttSubscribeMessage subscribe = MqttMessageBuilders.subscribe()
                    .addSubscription(AT_MOST_ONCE, topic)
                    .messageId(id)
                    .build();
            channel.writeAndFlush(subscribe);
        };

        return channels.size() + "";
    }


    private static io.netty.handler.codec.mqtt.MqttMessage pingMessage(String clientID, int keepAlive) {
        MqttFixedHeader pingHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, AT_MOST_ONCE,
                false, 0);
        io.netty.handler.codec.mqtt.MqttMessage pingReq = new io.netty.handler.codec.mqtt.MqttMessage(pingHeader);
        return pingReq;
    }

    Random r = new Random();

    @GetMapping("/send")
    String sendMesage(String topic) throws MqttException {
        MqttClient mqttClient = clients.get(r.nextInt(clients.size()));
        mqttClient.publish("/topic/"+topic, "Test my payload".getBytes(UTF_8), 0, false);

        return "send success";
    }

    @GetMapping("/send2")
    String sendMesage2(String topic,int count) throws MqttException {
        MqttClient mqttClient = clients.get(r.nextInt(clients.size()));
        for(int i=0;i<count;i++){
            mqttClient.publish("/topic/"+topic, "Test my payload".getBytes(UTF_8), 0, false);

        }
        return "send success";
    }

    @GetMapping("/send3")
    String sendMesage3(String topic) throws MqttException {
        String topic2 = "/topic/"+topic;
        logger.debug("发布的topic:{}",topic2);
        Channel mqttClient = channels.get(r.nextInt(channels.size()));
        MqttPublishMessage publish = MqttMessageBuilders.publish()
                .topicName(topic2)
                .retained(false)
                .qos(MqttQoS.AT_MOST_ONCE)
                .payload(Unpooled.copiedBuffer("Hello MQTT world!".getBytes(UTF_8))).build();
        mqttClient.writeAndFlush(publish);
        return "send success";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(){
            @Override
            public void run() {
               while (true){
                   try {
                       Thread.sleep(20000L);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   channels.forEach((c)->{
                       c.writeAndFlush(pingMessage("aaa",20)).addListener(FIRE_EXCEPTION_ON_FAILURE);
                   });
               }
            }
        }.start();
    }


    private class MessageCallback implements MqttCallbackExtended {
        private Logger logger = LoggerFactory.getLogger(MessageCallback.class);

        private MqttClient client;

        public MessageCallback(MqttClient client
        ) {
            this.client = client;
        }

        @Override
        public void connectionLost(Throwable cause) {
            logger.info("连接已断开:[{}]", client.getClientId());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            logger.info("收到消息[{}][{}]", client.getClientId(),topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            logger.info("发送完成");
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            logger.info("连接完成[{}] [{}]", client.getClientId(), reconnect);
        }
    }

}
