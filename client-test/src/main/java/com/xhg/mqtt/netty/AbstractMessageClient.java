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

package com.xhg.mqtt.netty;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

import com.sun.javafx.UnmodifiableArrayList;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttVersion;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author xieyang
 */
public abstract class AbstractMessageClient implements MessageClient {


    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AtomicInteger id = new AtomicInteger(0);


    protected ClientOptions options;

    protected String clientId;

    protected Channel channel;


    protected Bootstrap bootstrap;

    private int reconnectTimes;

    private int maxReconnectTimes = 10;

    protected Random random = new Random();

    private final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();



    private volatile static boolean pingTaskStart;


    public AbstractMessageClient(Bootstrap bootstrap, ClientOptions options, String clientId, Channel channel) {
        this.bootstrap = bootstrap;
        this.clientId = clientId;
        this.channel = channel;
        this.options = options;
        startPingTask(options.getKeepAlive());
    }

    void connect() {
        logger.info("clientId[{}] 发送连接消息:", clientId);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE,
                false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
                MqttVersion.MQTT_3_1.protocolName(), MqttVersion.MQTT_3_1.protocolLevel(), false, false, false, 1, false,
                true, options.getKeepAlive()+30);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientId, null, null,
                null, (byte[]) null);
        MqttConnectMessage message = new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
        send(message);
    }


    void ping() {
        MqttFixedHeader pingHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, AT_MOST_ONCE,
                false, 0);
        MqttMessage pingReq = new MqttMessage(pingHeader);
        send(pingReq);

    }

    void subscript() {
        logger.info("[{}] 订阅主题:{}", clientId, options.getTopics());

        MqttMessageBuilders.SubscribeBuilder subBuilder = MqttMessageBuilders.subscribe();
        for (String t : options.getTopics()) {
            subBuilder.addSubscription(AT_MOST_ONCE, t);
        }
        MqttSubscribeMessage message = subBuilder.messageId(createMessageId()).build();
        channel.writeAndFlush(message);
    }

    protected int createMessageId() {
        return id.incrementAndGet();
    }

    @Override
    public String getClientId() {
        return clientId;
    }


    @Override
    public void send(MqttMessage mqttMessage) {
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
            if (!channel.isActive() && reconnectTimes < maxReconnectTimes) {
                reconnectTimes++;
                reconnect(false);
            }
        }
    }


    @Override
    public void reconnect(boolean immediately) {
        if(!immediately){
            try {
                int step = (int) Math.pow(10,reconnectTimes);
                TimeUnit.MILLISECONDS.sleep(random.nextInt(step));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("[{}]重连=====>>>>", clientId);
        ClientOptions.Node node = options.getSelectNode();
        try {
            channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();
            channel.attr(ClientNettyMQTTHandler.ATTR_KEY_CLIENT_CHANNEL).set(AbstractMessageClient.this);
            connect();
            reconnectTimes = 0;
        } catch (Exception e) {
            logger.error("[{}]重连异常:", clientId, e);
            onClosed(null);
        }
    }


    protected synchronized void startPingTask(int keepAlive) {
        if (pingTaskStart) {
            return;
        }
        pingTaskStart = true;
        executorService.scheduleAtFixedRate(new PingTask(), 0, keepAlive, TimeUnit.SECONDS);
    }

    public class PingTask implements Runnable {

        @Override
        public void run() {
            UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getNettyChannels();
            logger.debug("ping channels:[{}]", nettyChannels.size());
            nettyChannels.forEach((c) -> {
                AbstractMessageClient messageClient = (AbstractMessageClient) c;
                messageClient.ping();
            });
        }
    }

    @Override
    public ClientOptions getOptions() {
        return options;
    }

    @Override
    public void disconnect() {
        if(channel.isOpen()){
            channel.close();
        }
    }


}
