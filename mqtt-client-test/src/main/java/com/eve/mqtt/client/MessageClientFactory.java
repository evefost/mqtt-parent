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

package com.eve.mqtt.client;

import com.eve.mqtt.common.SystemCmd;
import com.eve.mqtt.util.ClientUtils;
import com.sun.javafx.UnmodifiableArrayList;
import io.netty.bootstrap.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.eve.mqtt.common.Constants.SYSTEM_CONTROL_PATTERN;

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


    /**
     * @param isMainClient 主客户端与其它客户端添加的topic不太一样
     */
    public static <M extends MessageClient> M getAndCreateClient(Class<M> clientClass, boolean isMainClient) {
        return getAndCreateClient(clientClass, null, isMainClient);
    }

    /**
     * @param isMainClient isMainClient 主客户端与其它客户端添加的题题不太一样
     */
    public static synchronized  <M extends MessageClient> M  getAndCreateClient(Class<M> clientClass, List<String> speciallyTopics,
        boolean isMainClient) {
        if (commonOptions == null) {
            throw new RuntimeException("设置客户端配置");
        }

        ClientOptions options = commonOptions.clone();
        ClientOptions.Node node = ClientUtils.selectServerNode(commonOptions);
        options.setSelectNode(node);
        Bootstrap bootstrap = NettyClientStarter.getInstance().getBootstrap();

        String clientId = ClientUtils.createClientId();
        String pointTopic = "/topic/" + clientId;
        options.getTopics().add(pointTopic);
        if (speciallyTopics != null && !speciallyTopics.isEmpty()) {
            options.getTopics().addAll(speciallyTopics);
        }
        addSystemTopics(options, isMainClient);
        M instance = null;
        try {
            Constructor<? extends MessageClient> constructor = clientClass.getDeclaredConstructor(Bootstrap.class, ClientOptions.class, String.class);
            constructor.setAccessible(true);
            instance = (M) constructor.newInstance(bootstrap, options, clientId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        clients.add(instance);
        return instance;
    }


    /**
     * 添加系统topics
     */
    static void addSystemTopics(ClientOptions options, boolean isMainClient) {
        SystemCmd[] values = SystemCmd.values();
        for (SystemCmd cmd : values) {

            if (isMainClient) {
                options.getTopics().add(cmd.getTopic());
            } else if (!isMain(cmd)) {
                //普通连接不添加/SYSTEM_CONTROL_PATERN特征主题
                options.getTopics().add(cmd.getTopic());
            }
        }
    }

    static strictfp boolean isMain(SystemCmd cmd) {
        if (cmd.getTopic().startsWith(SYSTEM_CONTROL_PATTERN)) {
            return true;
        }
        return false;
    }


    public static UnmodifiableArrayList<MessageClient> getClients() {
        MessageClient[] array = new MessageClient[clients.size()];
        clients.toArray(array);
        return new UnmodifiableArrayList<>(array, array.length);
    }




    /**
     * 重置客户端，断开的连接将被收回，不会自动连
     */
    public synchronized static void reset(int count) {
        int disconnectCount = count <= 0 ? clients.size() : count;
        logger.info("正在断开客户端连接数[{}]", disconnectCount);
        List<MessageClient> aliveClients = new ArrayList<>();
        for (MessageClient client : clients) {
            if (disconnectCount < 1) {
                aliveClients.add(client);
            } else if (!(client instanceof SingletonClient)) {
                client.getOptions().setAutoReconnect(false);
                client.disconnect();
                disconnectCount--;
            }
        }
        clients.clear();
        clients.addAll(aliveClients);
        clientCount.set(clients.size()+1);
    }

    /**
     * disconnect 断开的连接不收回，将自动重连
     */
    public  static void disconnect(int count) {
        int disconnectCount = count <= 0 ? clients.size() : count;
        logger.info("正在重置客户端连接[{}]", disconnectCount);
        for (MessageClient client : clients) {
            if (disconnectCount < 1) {
                break;
            } else if (!(client instanceof SingletonClient)) {
                client.disconnect();
                disconnectCount--;
            }
        }
    }
}

