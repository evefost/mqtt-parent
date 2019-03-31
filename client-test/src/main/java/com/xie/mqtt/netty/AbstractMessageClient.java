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

package com.xie.mqtt.netty;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

/**
 * Class used just to send and receive MQTT messages without any protocol login in action, just use
 * the encoder/decoder part.
 * @author xieyang
 */
public abstract class AbstractMessageClient implements MessageClient {


    protected final static AtomicInteger clientCount = new AtomicInteger(0);

    protected   final Logger logger = LoggerFactory.getLogger(getClass());


    protected String clientId;

    protected Channel channel;

    protected String[] topics;

    private AtomicInteger id = new AtomicInteger(0);


    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void subscript() {
        logger.info("clientId[{}] 订阅主题:{}",clientId,topics);

        MqttMessageBuilders.SubscribeBuilder subBuilder = MqttMessageBuilders.subscribe();
        for(String t:topics){
            subBuilder.addSubscription(AT_MOST_ONCE, t);
        }
        MqttSubscribeMessage message = subBuilder.messageId(createMessageId()).build();
        channel.writeAndFlush(message);
    }

    protected  int createMessageId(){
       return id.incrementAndGet();
    }

    @Override
    public void connect() {
        logger.info("clientId[{}] 发起连接broker:",clientId);
        int keepAlive = 60;
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE,
                false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
                MqttVersion.MQTT_3_1.protocolName(), MqttVersion.MQTT_3_1.protocolLevel(), false, false, false, 1, false,
                true, keepAlive);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientId, null, null,
                null, (byte[]) null);
        MqttConnectMessage message =  new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
        send(message);
    }

    @Override
    public void send(MqttMessage mqttMessage) {
        channel.writeAndFlush(mqttMessage);
    }

    @Override
    public void onReceived(MqttMessage msg) {

    }


}
