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

package com.eve.broker.core.metrics;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;

import com.eve.broker.core.listener.MqttListener;
import com.eve.broker.core.NettyUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class MessageMetricsHandler extends ChannelDuplexHandler {

    private static final AttributeKey<MessageMetrics> ATTR_KEY_METRICS = AttributeKey.valueOf("MessageMetrics");

    private MessageMetricsCollector m_collector;

    private MqttListener mqttListener;

    public MessageMetricsHandler(MessageMetricsCollector collector, MqttListener mqttListener) {
        m_collector = collector;
        this.mqttListener = mqttListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Attribute<MessageMetrics> attr = ctx.channel().attr(ATTR_KEY_METRICS);
        attr.set(new MessageMetrics());
        mqttListener.open();
        super.channelActive(ctx);
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageMetrics metrics = ctx.channel().attr(ATTR_KEY_METRICS).get();
        metrics.incrementRead(1);
        mqttListener.input(msg);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        MessageMetrics metrics = ctx.channel().attr(ATTR_KEY_METRICS).get();
        metrics.incrementWrote(1);
        mqttListener.output(msg);
        ctx.write(msg, promise).addListener(CLOSE_ON_FAILURE);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        MessageMetrics metrics = ctx.channel().attr(ATTR_KEY_METRICS).get();
        m_collector.sumReadMessages(metrics.messagesRead());
        m_collector.sumWroteMessages(metrics.messagesWrote());

        super.close(ctx, promise);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientID = NettyUtils.clientID(ctx.channel());
        if (clientID != null && !clientID.isEmpty()) {
            mqttListener.close();
        }
        ctx.fireChannelInactive();
    }

    public static MessageMetrics getMessageMetrics(Channel channel) {
        return channel.attr(ATTR_KEY_METRICS).get();
    }
}
