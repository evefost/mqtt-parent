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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static io.netty.handler.codec.mqtt.MqttMessageType.*;

@ChannelHandler.Sharable
public class ClientNettyMQTTHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientNettyMQTTHandler.class);

    public static final String ATTR_CLIENT_CHANNEL = "client_channel";

    public static final AttributeKey<AbstractMessageClient> ATTR_KEY_CLIENT_CHANNEL = AttributeKey.valueOf(ATTR_CLIENT_CHANNEL);



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        MqttMessage msg = (MqttMessage) message;
        MqttFixedHeader mqttFixedHeader = msg.fixedHeader();
        AbstractMessageClient mqttChannel =  ctx.channel().attr(ATTR_KEY_CLIENT_CHANNEL).get();
        if (mqttFixedHeader.messageType() == PUBLISH) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) message;
            MqttPublishVariableHeader header = publishMessage.variableHeader();
            String topic = header.topicName();
            logger.info("[{}]收到消息:{}", mqttChannel.getClientId(), header.topicName());
            if("/topic/reset".equals(topic)){
                MessageClientFactory.reset();
            }else if("/topic/disconnect".equals(topic)){
                MessageClientFactory.closeAll();
            }else {
                mqttChannel.onReceived(topic,publishMessage);
            }
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
        MessageClient client = getClient(ctx);
        client.onClosed(null);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close().addListener(CLOSE_ON_FAILURE);
        MessageClient client = getClient(ctx);
        if(client != null){
            client.onClosed(cause);
        }
    }

    private MessageClient getClient(ChannelHandlerContext ctx){
        AbstractMessageClient mqttChannel =  ctx.channel().attr(ATTR_KEY_CLIENT_CHANNEL).get();
        return mqttChannel;
    }


}
