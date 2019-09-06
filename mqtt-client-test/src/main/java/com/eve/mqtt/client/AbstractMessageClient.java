/*
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package com.eve.mqtt.client;

import com.sun.javafx.UnmodifiableArrayList;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;


/**
 * @author xieyang
 */
public abstract class AbstractMessageClient implements MessageClient<MqttPublishMessage> {


    protected static final Logger logger = LoggerFactory.getLogger(AbstractMessageClient.class);

    private AtomicInteger id = new AtomicInteger(0);


    protected ClientOptions options;

    protected String clientId;

    protected volatile Channel channel;

    protected volatile boolean isReconnect;

    protected Bootstrap bootstrap;

    private int reconnectTimes;

    private int maxReconnectTimes = 30;

    protected Random random = new Random();

    private final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    private volatile static boolean pingTaskStart;


    public AbstractMessageClient(Bootstrap bootstrap, ClientOptions options, String clientId) {
        this.bootstrap = bootstrap;
        this.clientId = clientId;
        this.options = options;
        init();
    }


    @Override
    public void connect() {
        if (channel == null) {
            reconnect(false);
            return;
        }

        logger.info("clientId[{}] 发送mqtt连接[{}:{}]命令", clientId,options.getSelectNode().getHost(),options.getSelectNode().getPort());
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE,
                false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
                MqttVersion.MQTT_3_1.protocolName(), MqttVersion.MQTT_3_1.protocolLevel(), false, false, false, 1, false,
                true, options.getKeepAlive() + 30);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientId, null, null,
                null, (byte[]) null);
        MqttConnectMessage message = new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
        channel.writeAndFlush(message);
    }


    @Override
    public void subscript() {
        logger.info("[{}] 订阅主题:{}", clientId, options.getTopics());

        MqttMessageBuilders.SubscribeBuilder subBuilder = MqttMessageBuilders.subscribe();
        for (String t : options.getTopics()) {
            subBuilder.addSubscription(AT_MOST_ONCE, t);
        }
        MqttSubscribeMessage message = subBuilder.messageId(createMessageId()).build();
        channel.writeAndFlush(message);
    }


    void ping() {
        MqttFixedHeader pingHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, AT_MOST_ONCE,
                false, 0);
        MqttMessage pingReq = new MqttMessage(pingHeader);
        channel.writeAndFlush(pingReq);

    }

    protected int createMessageId() {
        return id.incrementAndGet();
    }

    @Override
    public String getClientId() {
        return clientId;
    }


    @Override
    public void send(MqttPublishMessage mqttMessage) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}]发送消息[{}}", clientId, mqttMessage.variableHeader().topicName());
        }
        channel.writeAndFlush(mqttMessage);
    }


    @Override
    public void onClosed(Throwable cause) {
        if (cause != null) {
            logger.error("[{}]连接异常关闭", clientId, cause);
        } else {
            logger.warn("[{}]连接关闭", clientId);
        }
        if (options.isAutoReconnect()) {
            if (channel != null && !channel.isActive() && reconnectTimes < maxReconnectTimes) {
                reconnect(false);
            }
        }
    }


    @Override
    public void reconnect(boolean immediately) {
        if (!immediately) {
            try {
                reconnectTimes++;
                int step = (int) Math.pow(5, reconnectTimes);
                TimeUnit.MILLISECONDS.sleep(random.nextInt(step));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(channel != null && channel.isOpen()){
            return;
        }
        ClientOptions.Node node = options.getSelectNode();
        if(isReconnect){
            logger.info("[{}] 重连接 >> [{}:{}]", clientId,node.getHost(),node.getPort());
        }else {
            logger.info("[{}] 连接 >> [{}:{}]", clientId,node.getHost(),node.getPort());
            isReconnect = true;
        }
        try {
            channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();
            channel.attr(ClientNettyMQTTHandler.ATTR_KEY_CLIENT_CHANNEL).set(AbstractMessageClient.this);
            connect();
            reconnectTimes = 0;
        } catch (Throwable e) {
            logger.error("[{}]重连异常:", clientId, e);
            onClosed(e);
        }
    }


    protected boolean connected() {
        if(channel == null){
            return false;
        }
        return channel.isActive();
    }


    protected synchronized void init() {
        try {
            connect();
        }catch (Throwable ex){
            logger.error("初始化连接服务失败");
        }
        if (pingTaskStart) {
            return;
        }
        pingTaskStart = true;
        executorService.scheduleAtFixedRate(new PingTask(), 0, options.getKeepAlive(), TimeUnit.SECONDS);
    }

    public class PingTask implements Runnable {

        @Override
        public void run() {
            UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getClients();
            logger.debug("ping channels:[{}]", nettyChannels.size());
            nettyChannels.forEach((c) -> {
                AbstractMessageClient messageClient = (AbstractMessageClient) c;

                if (messageClient.connected()) {
                    try {
                        messageClient.ping();
                    }catch (Throwable ex){
                        logger.debug("ping failure ", ex);
                    }
                } else {
                    reconnect(true);
                }

            });
        }
    }

    @Override
    public ClientOptions getOptions() {
        return options;
    }

    @Override
    public void disconnect() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }


}
