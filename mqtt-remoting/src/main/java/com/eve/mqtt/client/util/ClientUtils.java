package com.eve.mqtt.client.util;

import com.eve.mqtt.client.ClientOptions;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientUtils {

    private static AtomicInteger clientCount = new AtomicInteger(0);

    private static String host;

    private static volatile boolean isLoadHost;

    public static ClientOptions.Node selectServerNode(ClientOptions options) {
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

    public static String createClientId() {
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

}
