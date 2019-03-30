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

package com.xie.mqtt.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;
import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;
import static io.netty.handler.codec.mqtt.MqttMessageType.PUBLISH;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

@ChannelHandler.Sharable
class ClientNettyMQTTHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientNettyMQTTHandler.class);
    private Client m_client;

    private AtomicInteger clientCount = new AtomicInteger(0);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        MqttMessage msg = (MqttMessage) message;
        MqttFixedHeader mqttFixedHeader = msg.fixedHeader();
        if(mqttFixedHeader.messageType()==PUBLISH){
            MqttPublishMessage publishMessage = (MqttPublishMessage) message;
            MqttPublishVariableHeader mqttPublishVariableHeader = publishMessage.variableHeader();
            logger.info("收到消息:{}",mqttPublishVariableHeader.topicName());
        }else {
            logger.info("Received a message of type {}", msg.fixedHeader().messageType());
        }


        //m_client.messageReceived(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("激活一个通道");
        int keepAlive = 20; // secs
        MqttConnectMessage connectMessage = createConnectMessage("device-"+clientCount.incrementAndGet(), keepAlive);
        Channel channel = ctx.channel();
        /*
         * ConnectMessage connectMessage = new ConnectMessage();
         * connectMessage.setProtocolVersion((byte) 3); connectMessage.setClientID("FAKECLNT");
         * connectMessage.setKeepAlive(keepAlive);
         */
        channel.writeAndFlush(connectMessage).addListener(FIRE_EXCEPTION_ON_FAILURE);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("通道关闭");
        m_client.setConnectionLost(true);
        ctx.close().addListener(CLOSE_ON_FAILURE);
    }

    void setClient(Client client) {
        m_client = client;
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
