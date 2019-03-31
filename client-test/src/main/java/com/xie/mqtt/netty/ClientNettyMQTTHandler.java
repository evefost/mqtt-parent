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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static io.netty.handler.codec.mqtt.MqttMessageType.*;

@ChannelHandler.Sharable
public class ClientNettyMQTTHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientNettyMQTTHandler.class);
    public static final String ATTR_CLIENT_CHANNEL = "client_channel";
    public static final AttributeKey<Object> ATTR_KEY_CLIENT_CHANNEL = AttributeKey.valueOf(ATTR_CLIENT_CHANNEL);


    private AtomicInteger clientCount = new AtomicInteger(0);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        MqttMessage msg = (MqttMessage) message;
        MqttFixedHeader mqttFixedHeader = msg.fixedHeader();
        MessageClient mqttChannel = (MessageClient) ctx.channel().attr(ATTR_KEY_CLIENT_CHANNEL).get();
        if (mqttFixedHeader.messageType() == PUBLISH) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) message;
            MqttPublishVariableHeader mqttPublishVariableHeader = publishMessage.variableHeader();
            logger.info("[{}]收到消息:{}", mqttChannel.getClientId(), mqttPublishVariableHeader.topicName());
            mqttChannel.onReceived(msg);
        } else if (mqttFixedHeader.messageType() == CONNACK) {

            logger.info("[{}] 连接成功", mqttChannel.getClientId());
            mqttChannel.subscript();

        } else if (mqttFixedHeader.messageType() == SUBACK) {
            logger.info("[{}] 订阅成功", mqttChannel.getClientId());

        } else {
            logger.info("Received a message of type {}", msg.fixedHeader().messageType());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel active");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("通道关闭");
        ctx.close().addListener(CLOSE_ON_FAILURE);
    }


    private static MqttConnectMessage createConnectMessage(String clientID, int keepAlive) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE,
                false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
                MqttVersion.MQTT_3_1.protocolName(), MqttVersion.MQTT_3_1.protocolLevel(), false, false, false, 1, false,
                true, keepAlive);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientID, null, null,
                null, (byte[]) null);
        return new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
    }


}
