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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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


    public static <M extends MessageClient> M getAndCreateChannel(Class<M> clientClass) {
        return getAndCreateChannel(clientClass, null);
    }

    public static <M extends MessageClient> M getAndCreateChannel(Class<M> clientClass, List<String> speciallyTopics) {
        if (commonOptions == null) {
            throw new RuntimeException("设置客户端配置");
        }

        ClientOptions options = commonOptions.clone();
        ClientOptions.Node node = selectNode(commonOptions);
        options.setSelectNode(node);
        Bootstrap bootstrap = NettyClientStarter.getInstance().getBootstrap();

        String clientId = createClientId();
        String pointTopic = "/topic/" + clientId;
        options.getTopics().add(pointTopic);
        if (speciallyTopics != null && !speciallyTopics.isEmpty()) {
            options.getTopics().addAll(speciallyTopics);
        }
        addSystemTopics(options);


        Channel channel = null;
        try {
            channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        M instance = null;
        try {
            Constructor<? extends MessageClient> constructor = clientClass.getDeclaredConstructor(Bootstrap.class, ClientOptions.class, String.class, Channel.class);
            constructor.setAccessible(true);
            instance = (M) constructor.newInstance(bootstrap, options, clientId, channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        channel.attr(ClientNettyMQTTHandler.ATTR_KEY_CLIENT_CHANNEL).set(instance);
        instance.connect();
        clients.add(instance);
        return instance;
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
            if (!(client instanceof SingletonClient)) {
                client.getOptions().setAutoReconnect(false);
                client.disconnect();
            }
        });
        clients.clear();
        clients.add(SingletonClient.getInstance());
        clientCount.set(1);
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
