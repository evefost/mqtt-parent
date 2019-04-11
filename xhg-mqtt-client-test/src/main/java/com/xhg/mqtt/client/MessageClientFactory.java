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

import static com.xhg.mqtt.common.Constants.SYSTEM_CONTROL_PATTERN;

import com.sun.javafx.UnmodifiableArrayList;
import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.util.ServerUtils;
import io.netty.bootstrap.Bootstrap;
import java.lang.reflect.Constructor;
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


    /**
     * @param isMainClient 主客户端与其它客户端添加的题题不太一样
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
        ClientOptions.Node node = selectNode(commonOptions);
        options.setSelectNode(node);
        Bootstrap bootstrap = NettyClientStarter.getInstance().getBootstrap();

        String clientId = createClientId();
        String pointTopic = "/topic/" + clientId;
        options.getTopics().add(pointTopic);
        if (speciallyTopics != null && !speciallyTopics.isEmpty()) {
            options.getTopics().addAll(speciallyTopics);
        }
        addSystemTopics(options, isMainClient);
        M instance = null;
        try {
            Constructor<? extends MessageClient> constructor = clientClass
                .getDeclaredConstructor(Bootstrap.class, ClientOptions.class, String.class);
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

