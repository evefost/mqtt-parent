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
import io.netty.channel.Channel;
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
 * @author xieyang
 */
public class MessageClientFactory {


    private static final Logger logger = LoggerFactory.getLogger(MessageClientFactory.class);


    private static List<MessageClient> nettyChannels = new ArrayList<>();

    private static AtomicInteger clientCount = new AtomicInteger(0);



    public static MessageClient getAndCreateChannel(ClientOptions options) throws InterruptedException, CloneNotSupportedException {
        ClientOptions.Node node = selectNode(options);
        Bootstrap bootstrap = NettyClientStarter.getInstance().getBootstrap();
        Channel channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();
        String clientId = createClientId();
        String pointTopic="/topic/"+clientId;
        String broadcastTopic = "/topic/all";
        ClientOptions clone = options.clone();
        clone.setAutoReconnect(true);
        String[] topics = {pointTopic,broadcastTopic};
        clone.setTopics(topics);
        clone.setSelectNode(node);
        MessageClient client = new MqttNettyClient(bootstrap,clone,clientId,channel);
        channel.attr(ATTR_KEY_CLIENT_CHANNEL).set(client);
        client.connect();
        nettyChannels.add(client);
        return  client;
    }


    public static UnmodifiableArrayList<MessageClient> getNettyChannels() {
        MessageClient[] array = new MessageClient[nettyChannels.size()];
        nettyChannels.toArray(array);
        return new UnmodifiableArrayList<>(array,array.length);
    }

    public static String createClientId(){
        String id = "client-"+clientCount.incrementAndGet();
        return id;

    }

    public static ClientOptions.Node selectNode(ClientOptions options){
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
