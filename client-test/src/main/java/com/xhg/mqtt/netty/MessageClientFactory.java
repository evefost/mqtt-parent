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

import com.sun.javafx.UnmodifiableArrayList;
import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.util.ServerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used just to send and receive MQTT messages without any protocol login in action, just use the encoder/decoder
 * part.
 *
 * @author xieyang
 */
public class MessageClientFactory {


    private static final Logger logger = LoggerFactory.getLogger(MessageClientFactory.class);


    private volatile static List<MessageClient> clients = new ArrayList<>();

    private static AtomicInteger clientCount = new AtomicInteger(0);


    private static ClientOptions commonOptions;

    private static String host;

    private static volatile boolean isLoadHost;

    public static void setCommonOptoins(ClientOptions options) {
        commonOptions = options;
    }

    public static ClientOptions getCommonOptoins() {
        return commonOptions;
    }

    public static MessageClient getAndCreateChannel(ClientOptions srcOptions)
        throws InterruptedException, CloneNotSupportedException {
        ClientOptions options = srcOptions.clone();
        ClientOptions.Node node = selectNode(srcOptions);
        options.setSelectNode(node);
        Bootstrap bootstrap = NettyClientStarter.getInstance().getBootstrap();
        Channel channel = bootstrap.connect(options.getSelectNode().getHost(), options.getSelectNode().getPort()).sync()
            .channel();
        String clientId = createClientId();
        String pointTopic = "/topic/" + clientId;
        options.getTopics().add(pointTopic);
        addSystemTopics(options);
        MqttNettyClient client = new MqttNettyClient(bootstrap, options, clientId, channel);
        channel.attr(ClientNettyMQTTHandler.ATTR_KEY_CLIENT_CHANNEL).set(client);
        client.connect();
        clients.add(client);
        return client;
    }

    /**
     * 添加系统topics
     */
    static void addSystemTopics(ClientOptions options) {
        SystemCmd[] values = SystemCmd.values();
        for (SystemCmd cmd : values) {
            if (cmd != SystemCmd.TEST_INCREASE_CLIENT) {
                options.getTopics().add(cmd.getTopic());
            }
        }
    }


    public static UnmodifiableArrayList<MessageClient> getNettyChannels() {
        MessageClient[] array = new MessageClient[clients.size()];
        clients.toArray(array);
        return new UnmodifiableArrayList<>(array, array.length);
    }


    public static ClientOptions.Node selectNode(ClientOptions options) {
        String node = null;
        if (options.getBrokerNodes().length == 1) {
            node = options.getBrokerNodes()[0];
        } else {
            Random random = new Random();
            node = options.getBrokerNodes()[random.nextInt(options.getBrokerNodes().length)];
        }
        String[] nodeInfo = node.split(":");
        ClientOptions.Node node1 = new ClientOptions.Node();
        node1.setHost(nodeInfo[0]);
        node1.setPort(Integer.parseInt(nodeInfo[1]));
        return node1;

    }


    static String createClientId() {
        if (isLoadHost) {
            return host + "-" + clientCount.incrementAndGet();
        }
        try {
            InetAddress localHostLANAddress = ServerUtils.getLocalHostLANAddress();
            host = localHostLANAddress.getHostAddress();
            isLoadHost = true;
            String clientId = host + "-" + clientCount.incrementAndGet();
            return clientId;
        } catch (Exception e) {
        }
        String clientId = "device-" + clientCount.incrementAndGet();
        return clientId;
    }


    public synchronized static void reset() {
        logger.info("重置所有客户端:", clients.size());
        clients.forEach((client) -> {
            client.getOptions().setAutoReconnect(false);
            client.disconnect();
        });
        clients = null;
        clients = new ArrayList<>();
        clientCount.set(0);
    }

    private static volatile boolean isCloseAllIng;

    public static void closeAll() {
        //关闭中，所有请求都丢掉
        if (isCloseAllIng) {
            return;
        }
        isCloseAllIng = true;
        logger.info("正在关闭所有客户端:", clients.size());
        clients.forEach((client) -> {
            client.disconnect();
        });
        isCloseAllIng = false;
    }


}