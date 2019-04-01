package com.xhg.mqtt.mq.client;

import com.xhg.core.util.ServerUtils;

import com.xhg.mqtt.mq.EventCodeEnum;
import com.xhg.mqtt.mq.MqttMessageListener;
import com.xhg.mqtt.mq.proto.MqttMessagePb;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttHead;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFactory {

    private static Logger logger = LoggerFactory.getLogger(ClientFactory.class);

    public static List<MessageClient> consumerClients = new ArrayList<>();

    public static List<MessageClient> producerClients = new ArrayList<>();


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    private static volatile AtomicInteger clientCount = new AtomicInteger(0);

    private static Random r = new Random();

    public static MessageClient createClient(MqttConfig mqttConfig,boolean consumer) {
        String[] nodes = mqttConfig.getNodes();

        String selectNode = nodes[r.nextInt(nodes.length)];
        String clientId = clientId(mqttConfig);
        MqttClient client = null;
        try {
            client = new MqttClient(selectNode, clientId, new MemoryPersistence());
            client.setCallback(new MqttMessageListener(client, mqttConfig.getTopics(),consumer));
            client.connect(getOption(clientId, mqttConfig));
        } catch (MqttException e) {
            logger.error(String.format("mqtt客户端创建失败， clientId[%s], message[%s]", clientId, e.getMessage()));
        } catch (Throwable throwable) {
            logger.error("mqtt客户端创建失败{}/", mqttConfig.getNodes(), throwable);
        }
        MqttWrapperClient wrapperClient = new MqttWrapperClient(client,consumer);
        return wrapperClient;

    }

    public static void addClient(MessageClient client,boolean consumer){
        if(consumer){
            consumerClients.add(client);
        }else {
            producerClients.add(client);
        }
    }

    public static MessageClient getClient(boolean consumer){
        if(consumer){
            return   consumerClients.get(r.nextInt(consumerClients.size()));
        }else {
            return   producerClients.get(r.nextInt(producerClients.size()));
        }

    }

    public static int getClientSize(boolean consumer){
        if(consumer){
            return   consumerClients.size();
        }else {
            return   producerClients.size();
        }
    }

    public static String clientId(MqttConfig mqttConfig) {

        try {
            InetAddress localHostLANAddress = ServerUtils.getLocalHostLANAddress();
            String clientId = mqttConfig.getClientIdPrefix()+localHostLANAddress.getHostAddress() + "-" + clientCount.incrementAndGet();
            return clientId;
        } catch (Exception e) {

        }
        String clientId = mqttConfig.getClientIdPrefix() + clientCount.incrementAndGet();
        return clientId;
    }

    /**
     * 获取连接配置 chenxiaojun 2018年8月22日
     */
    private static MqttConnectOptions getOption(String clientId, MqttConfig mqttConfig) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(mqttConfig.getNodes());
        options.setCleanSession(true);
        options.setKeepAliveInterval(30);
        options.setConnectionTimeout(15);

        options.setUserName(mqttConfig.getUsername());
        options.setPassword(mqttConfig.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        MqttMessagePb.MqttMessage.Builder builder = MqttMessagePb.MqttMessage.newBuilder();
        MqttHead.Builder header = MqttHead.newBuilder();
        header.setEventCode(EventCodeEnum.DEVICE_WILL.getCode());
        header.setMessageId(UUID.randomUUID().toString());
        header.setDeviceId(clientId);
        header.setVersion("1.0.0");
        header.setResultCode(1);
        builder.setHead(header);
        MqttMessage mqttMessage = builder.build();
        options.setWill(mqttConfig.getWillTopic() + clientId, mqttMessage.toByteArray(), 1, false);
        return options;
    }

}
