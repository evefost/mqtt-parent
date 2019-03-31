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

import com.sun.javafx.UnmodifiableArrayList;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xie.mqtt.netty.ClientNettyMQTTHandler.ATTR_KEY_CLIENT_CHANNEL;

/**
 * Class used just to send and receive MQTT messages without any protocol login in action, just use
 * the encoder/decoder part.
 */
public class MultiChannelClient {


    private static final Logger LOG = LoggerFactory.getLogger(MultiChannelClient.class);

    final ClientNettyMQTTHandler handler = new ClientNettyMQTTHandler();

    EventLoopGroup workerGroup;

    Bootstrap bootstrap;


    List<MessageClient> nettyChannels = new ArrayList<>();

    private AtomicInteger clientCount = new AtomicInteger(0);

    private ClientOptions options;

    public MultiChannelClient(ClientOptions options) {
        this.options = options;
        this.init();

    }

    private void init(){

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
        } catch (Exception ex) {
            LOG.error("Error received in client setup", ex);
            workerGroup.shutdownGracefully();
        }
    }

    public MessageClient getAndCreateChannel() throws InterruptedException, CloneNotSupportedException {
        ClientOptions.Node node = selectNode();

        Channel channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();
        String clientId = createClientId();
        String pointTopic="/topic/"+clientId;
        String broadcastTopic = "/topic/all";
        ClientOptions clone = options.clone();
        clone.setAutoReconnect(true);
        String[] topics = {pointTopic,broadcastTopic};
        clone.setTopics(topics);
        clone.setSelectNode(node);
        MessageClient client = new MqttNettyClient(clone,clientId,channel,bootstrap);
        channel.attr(ATTR_KEY_CLIENT_CHANNEL).set(client);
        client.connect();
        nettyChannels.add(client);
        return  client;
    }


    public UnmodifiableArrayList<MessageClient> getNettyChannels() {
        MessageClient[] array = new MessageClient[nettyChannels.size()];
        nettyChannels.toArray(array);
        return new UnmodifiableArrayList<>(array,array.length);
    }

    public String createClientId(){
        String id = "client-"+clientCount.incrementAndGet();
        return id;

    }

    private ClientOptions.Node selectNode(){
        String node = null;
        if(options.getBrokerNodes().length==1){
            node = options.getBrokerNodes()[0];
        }else {
            Random random = new Random();
            node = options.getBrokerNodes()[random.nextInt(options.getBrokerNodes().length)];
        }
        String[] nodeInfo = node.split(":");
        ClientOptions.Node node1 = new ClientOptions.Node();
        node1.setHost(nodeInfo[0]);
        node1.setPort(Integer.parseInt(nodeInfo[1]));
        return node1;

    }



}
