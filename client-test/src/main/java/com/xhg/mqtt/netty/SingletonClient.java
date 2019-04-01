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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used just to send and receive MQTT messages without any protocol login in action, just use
 * the encoder/decoder part.
 */
public class SingletonClient extends AbstractMessageClient {

    private static SingletonClient instance;

    private SingletonClient() {
        this(null, null, null, null);
    }

    private SingletonClient(Bootstrap bootstrap, ClientOptions options, String clientId, Channel channel) {
        super(bootstrap, options, clientId, channel);

    }

    public static SingletonClient getInstance(ClientOptions options) throws InterruptedException, CloneNotSupportedException {
        if (instance == null) {
            synchronized (NettyClientStarter.class) {
                if (instance == null) {
                    instance = createInstance(options);
                }
            }
        }
        return instance;
    }

    private static SingletonClient createInstance(ClientOptions options) throws InterruptedException, CloneNotSupportedException {
        ClientOptions.Node node = MessageClientFactory.selectNode(options);
        options.setSelectNode(node);
        String clientId = MessageClientFactory.createClientId();
        String pointTopic="/topic/"+clientId;
        options.getTopics().add(pointTopic);
        options.setSelectNode(node);
        Bootstrap bootstrap = NettyClientStarter.getInstance().getBootstrap();
        Channel channel = bootstrap.connect(options.getSelectNode().getHost(), options.getSelectNode().getPort()).sync().channel();
        SingletonClient singletonClient = new SingletonClient(bootstrap, options, clientId, channel);
        channel.attr(ClientNettyMQTTHandler.ATTR_KEY_CLIENT_CHANNEL).set(singletonClient);
        singletonClient.connect();
        return singletonClient;
    }


    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void onReceived(String topic,MqttPublishMessage msg) {

    }




}
