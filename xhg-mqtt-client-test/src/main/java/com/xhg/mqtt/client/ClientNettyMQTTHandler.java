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

package com.xhg.mqtt.client;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;
import static io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader.from;
import static io.netty.handler.codec.mqtt.MqttMessageType.CONNACK;
import static io.netty.handler.codec.mqtt.MqttMessageType.PUBLISH;
import static io.netty.handler.codec.mqtt.MqttMessageType.SUBACK;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

import com.xhg.mqtt.common.handler.HandlerDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ClientNettyMQTTHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientNettyMQTTHandler.class);

    public static final String ATTR_CLIENT_CHANNEL = "client_channel";

    public static final AttributeKey<MessageClient> ATTR_KEY_CLIENT_CHANNEL = AttributeKey
        .valueOf(ATTR_CLIENT_CHANNEL);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        MqttMessage msg = (MqttMessage) message;
        MqttFixedHeader mqttFixedHeader = msg.fixedHeader();
        MessageClient mqttChannel = ctx.channel().attr(ATTR_KEY_CLIENT_CHANNEL).get();
        if (mqttFixedHeader.messageType() == PUBLISH) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) message;
            MqttPublishVariableHeader header = publishMessage.variableHeader();
            String topic = header.topicName();
            final int messageID = header.packetId();
            logger.info("[{}]收到消息:[{}][{}]", mqttChannel.getClientId(), messageID, header.topicName());
            //sendPubAck(ctx.channel(),messageID);
            if (!HandlerDispatcher.process(msg)) {
                mqttChannel.onReceived(topic, publishMessage);
            }
        } else if (mqttFixedHeader.messageType() == CONNACK) {
            logger.info("[{}] 连接成功", mqttChannel.getClientId());
            mqttChannel.subscript();

        } else if (mqttFixedHeader.messageType() == SUBACK) {
            logger.info("[{}] 订阅成功", mqttChannel.getClientId());

        } else {
            logger.debug("Received a message of type {}", msg.fixedHeader().messageType());
        }
    }

    private void increaseClients(MqttPublishMessage mqttMessage) {
        ByteBuf byteBuf = mqttMessage.payload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
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
        if (client != null) {
            client.onClosed(cause);
        }
    }

    private MessageClient getClient(ChannelHandlerContext ctx) {
        MessageClient mqttChannel = ctx.channel().attr(ATTR_KEY_CLIENT_CHANNEL).get();
        return mqttChannel;
    }

    void sendPubAck(Channel channel, int messageID) {
        logger.trace("sendPubAck invoked");
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, AT_MOST_ONCE,
            false, 0);
        MqttPubAckMessage pubAckMessage = new MqttPubAckMessage(fixedHeader, from(messageID));
        sendIfWritableElseDrop(channel, pubAckMessage);
    }

    void sendIfWritableElseDrop(Channel channel, MqttMessage msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("OUT {} on channel {}", msg.fixedHeader().messageType(), channel);
        }
        if (channel.isWritable()) {
            ChannelFuture channelFuture;
            channelFuture = channel.writeAndFlush(msg);
            channelFuture.addListener(FIRE_EXCEPTION_ON_FAILURE);
        }
    }
}
