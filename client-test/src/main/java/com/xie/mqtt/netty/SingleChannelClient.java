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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used just to send and receive MQTT messages without any protocol login in action, just use
 * the encoder/decoder part.
 */
public class SingleChannelClient extends AbstractMessageClient {


    private  final Logger logger = LoggerFactory.getLogger(getClass());

    final ClientNettyMQTTHandler handler = new ClientNettyMQTTHandler();

    EventLoopGroup workerGroup;


    Bootstrap bootstrap;

    private String host;

    private int port;


    public SingleChannelClient(String host, int port) {
        this.host = host;
        this.port = port;


        workerGroup = new NioEventLoopGroup(20);
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("decoder", new MqttDecoder());
                    pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    pipeline.addLast("handler", handler);
                }
            });
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception ex) {
            logger.error("Error received in client setup", ex);
            workerGroup.shutdownGracefully();
        }
    }

    public SingleChannelClient clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }


    @Override
    public String getClientId() {
        return clientId;
    }


}
