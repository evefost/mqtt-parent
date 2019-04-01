package com.xhg.mqtt.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.protobuf.ByteString;
import com.sun.javafx.UnmodifiableArrayList;
import com.xhg.core.web.vo.ResponseBean;
import com.xhg.mqtt.mq.EventCodeEnum;
import com.xhg.mqtt.mq.proto.BoxInfoPb.BoxInfo;
import com.xhg.mqtt.mq.proto.BoxInfoPb.BoxStatus;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttHead;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage.Builder;
import com.xhg.mqtt.netty.ClientOptions;
import com.xhg.mqtt.netty.MessageClient;
import com.xhg.mqtt.netty.MessageClientFactory;
import com.xhg.mqtt.netty.SingletonClient;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xieyang on 19/3/30.
 */
@RestController
@RequestMapping("client")
public class MqttClientController implements InitializingBean {


    private static final Logger logger = LoggerFactory.getLogger(MqttClientController.class);

    ExecutorService executorService = new ThreadPoolExecutor(10, 10000, 3000, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());

    private Random r = new Random();

    @Value("${product-key:}")
    private String productKey;

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
        return MessageClientFactory.getNettyChannels().size() + "";
    }

    @GetMapping("/create")
    String createNettyClient() throws MqttException, InterruptedException, CloneNotSupportedException {
        SingletonClient instance = SingletonClient.getInstance(options);
        return instance.getClientId();
    }

    @GetMapping("/send2")
    String send2(String topic) throws MqttException, CloneNotSupportedException, InterruptedException {
        String topic2 = "/topic/client-" + topic;
        logger.debug("发布的topic:{}", topic2);
        MessageClient mqttClient = SingletonClient.getInstance(options);
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(topic2)
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer("ssssHello MQTT world!".getBytes(UTF_8))).build();
        mqttClient.send(publish);
        return "send success";
    }


    @GetMapping("/send")
    String send(String topic) throws MqttException {
        String topic2 = "/topic/client-" + topic;
        logger.debug("发布的topic:{}", topic2);
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
        logger.debug("发布的topic:{}", topic2);
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

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getNettyChannels();
                    nettyChannels.forEach((c) -> {
                        c.ping();
                    });
                }
            }
        }.start();
    }


    @RequestMapping(value = "/sendBoxInfo", method = RequestMethod.GET)
    public ResponseBean sendBoxInfo() throws MqttException {

        UnmodifiableArrayList<MessageClient> clients = MessageClientFactory.getNettyChannels();

        for (MessageClient client : clients) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String clientId = client.getClientId();
                    String topic = productKey + "/server/a/b/c/" + clientId;
                    Builder builder = buildBoxMessage(clientId);
                    MqttMessage message = builder.build();
                    byte[] payload = message.toByteArray();
                    MqttPublishMessage publish = MqttMessageBuilders.publish()
                        .topicName(topic)
                        .retained(false)
                        .qos(MqttQoS.AT_MOST_ONCE)
                        .payload(Unpooled.copiedBuffer(payload)).build();
                    client.send(publish);
                }
            });

        }
        return ResponseBean.success(clients.size());
    }

    private Builder buildBoxMessage(String clientId) {
        Builder messageBuilder = MqttMessage.newBuilder();
        MqttHead.Builder headBuilder = MqttHead.newBuilder();
        headBuilder.setDeviceId(clientId);
        headBuilder.setMessageId(UUID.randomUUID().toString());
        headBuilder.setCc(1);
        headBuilder.setEventCode(EventCodeEnum.BOX_INFO.getCode());
        messageBuilder.setHead(headBuilder);

        BoxInfo.Builder boxbuild = BoxInfo.newBuilder();
        BoxStatus.Builder statusBuilder = BoxStatus.newBuilder();
        statusBuilder.setBoxStatus(1);
        statusBuilder.setTemperature(1.5d);
        BoxStatus status1 = statusBuilder.build();
        BoxStatus.Builder statusBuilder2 = BoxStatus.newBuilder();
        statusBuilder2.setBoxStatus(3);
        statusBuilder2.setTemperature(2.3d);
        BoxStatus status2 = statusBuilder2.build();
        boxbuild.addBoxStatus(status1);
        boxbuild.addBoxStatus(status2);

        BoxInfo boxInfo = boxbuild.build();
        ByteString box = boxInfo.toByteString();
        messageBuilder.setBody(box);
        return messageBuilder;
    }


}
