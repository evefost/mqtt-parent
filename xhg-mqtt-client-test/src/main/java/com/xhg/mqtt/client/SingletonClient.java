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

import io.netty.bootstrap.Bootstrap;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import java.util.ArrayList;
import java.util.List;

import static com.xhg.mqtt.common.SystemCmd.TEST_INCREASE_CLIENT;

/**
 * Class used just to send and receive MQTT messages without any protocol login in action, just use
 * the encoder/decoder part.
 */
public class SingletonClient extends AbstractMessageClient {

    private static SingletonClient instance;

     private SingletonClient() {
        this(null, null, null);
    }

     private SingletonClient(Bootstrap bootstrap, ClientOptions options, String clientId) {
        super(bootstrap, options, clientId);

    }

    public static SingletonClient getInstance()  {
        if (instance == null) {
            synchronized (NettyClientStarter.class) {
                if (instance == null) {
                    instance = createInstance();
                }
            }
        }
        return instance;
    }

    private static SingletonClient createInstance(){
        List<String> speciallyTopics = new ArrayList<>(1);
        speciallyTopics.add(TEST_INCREASE_CLIENT.getTopic());
        SingletonClient singletonClient = MessageClientFactory.getAndCreateClient(SingletonClient.class, speciallyTopics,true);
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
