package com.xie.mqtt.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.protobuf.ByteString;
import com.sun.javafx.UnmodifiableArrayList;
import com.xie.mqtt.client.ClientOptions;
import com.xie.mqtt.client.MessageClient;
import com.xie.mqtt.client.MessageClientFactory;
import com.xie.mqtt.client.MqttNettyClient;
import com.xie.mqtt.client.SingletonClient;
import com.xie.mqtt.common.EventCodeEnum;
import com.xie.mqtt.common.proto.BoxInfoPb.BoxInfo;
import com.xie.mqtt.common.proto.BoxInfoPb.BoxStatus;
import com.xie.mqtt.common.proto.MqttMessagePb.MqttHead;
import com.xie.mqtt.common.proto.MqttMessagePb.MqttMessage;
import com.xie.mqtt.common.proto.MqttMessagePb.MqttMessage.Builder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
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
public class MqttClientController implements SmartInitializingSingleton {


    private static final Logger logger = LoggerFactory.getLogger(MqttClientController.class);

    ExecutorService executorService = new ThreadPoolExecutor(10, 10000, 3000, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    private Random r = new Random();

    @Value("${product-key:}")
    private String productKey;

    @Value("${mqtt.broker.node:}")
    private String node;

    private ClientOptions options;

    private AtomicInteger id = new AtomicInteger(0);

    @GetMapping("/create/netty")
    String createNettyClient(int count) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            try {
                MessageClientFactory.getAndCreateClient(MqttNettyClient.class,false);
            } catch (Exception e) {
                logger.error("连接通道失败", e);

            }
        }
        return MessageClientFactory.getClients().size() + "";
    }

    @GetMapping("/create")
    String createNettyClient() throws InterruptedException, CloneNotSupportedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        SingletonClient instance = SingletonClient.getInstance();
        return instance.getClientId();
    }

    @GetMapping("/send2")
    String send2(String topic) throws CloneNotSupportedException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String topic2 = "/topic/client-" + topic;
        logger.debug("发布的topic:{}", topic2);
        ClientOptions clone = options.clone();
        clone.setAutoReconnect(true);
        MessageClient mqttClient = SingletonClient.getInstance();
        MqttPublishMessage publish = MqttMessageBuilders.publish()
                .topicName(topic2)
                .retained(false)
                .qos(MqttQoS.AT_MOST_ONCE)
                .payload(Unpooled.copiedBuffer("ssssHello MQTT world!".getBytes(UTF_8))).build();
        mqttClient.send(publish);
        return "send success";
    }


    @GetMapping("/send")
    String send(String topic) throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, CloneNotSupportedException, IllegalAccessException {
        String topic2 = "/topic/client-" + topic;
        logger.debug("发布的topic:{}", topic2);
        MessageClient client = SingletonClient.getInstance();
        Builder builder = buildBoxMessage(client.getClientId());
        MqttMessage message = builder.build();
        byte[] payload = message.toByteArray();
        MqttPublishMessage publish = MqttMessageBuilders.publish()
                .topicName(topic2)
                .messageId(id.incrementAndGet())
                .retained(false)
                .qos(MqttQoS.AT_LEAST_ONCE)
                .payload(Unpooled.copiedBuffer(payload)).build();
        client.send(publish);
        return "send success";
    }



    @GetMapping("/login")
    String login() throws InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, CloneNotSupportedException, IllegalAccessException {

        MessageClient client = SingletonClient.getInstance();
        String topic = productKey + "/server/a/b/c/" + client.getClientId();
        Builder builder = buildLoginMessage(client.getClientId());
        MqttMessage message = builder.build();
        byte[] payload = message.toByteArray();
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(topic)
            .messageId(id.incrementAndGet())
            .retained(true)
            .qos(MqttQoS.AT_LEAST_ONCE)
            .payload(Unpooled.copiedBuffer(payload)).build();
        client.send(publish);
        return "send success";
    }


    @GetMapping("/broadcast")
    String broadcast() {
        String topic2 = "/topic/all";
        logger.debug("发布的topic:{}", topic2);
        UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getClients();
        MessageClient client = nettyChannels.get(r.nextInt(nettyChannels.size()));
        Builder builder = buildBoxMessage(client.getClientId());
        MqttMessage message = builder.build();
        byte[] payload = message.toByteArray();
        MqttPublishMessage publish = MqttMessageBuilders.publish()
                .topicName(topic2)
                .retained(true)
                .qos(MqttQoS.AT_LEAST_ONCE)
                .payload(Unpooled.copiedBuffer(payload)).build();
        client.send(publish);
        return "send success";
    }


    @RequestMapping(value = "/sendBoxInfo", method = RequestMethod.GET)
    public String sendBoxInfo() {

        UnmodifiableArrayList<MessageClient> clients = MessageClientFactory.getClients();

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
        return "" + clients.size();
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

    private Builder buildLoginMessage(String clientId) {
        Builder messageBuilder = MqttMessage.newBuilder();
        MqttHead.Builder headBuilder = MqttHead.newBuilder();
        headBuilder.setDeviceId(clientId);
        headBuilder.setMessageId(UUID.randomUUID().toString());
        headBuilder.setCc(1);
        headBuilder.setEventCode(EventCodeEnum.DEVICE_LOGIN.getCode());
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

    private void initClientOptions() {
        options = new ClientOptions();
        options.setKeepAlive(90);
        options.setAutoReconnect(true);
        String[] nodes = {node};
        options.setBrokerNodes(nodes);
        List<String> topics = new ArrayList<>(10);
        options.setAutoReconnect(true);
        options.setTopics(topics);
        MessageClientFactory.setCommonOptoins(options);
        try {
            SingletonClient.getInstance();
        } catch (Throwable e) {
            logger.warn("连接客户端初始化失败:", e);
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        initClientOptions();
    }
}
