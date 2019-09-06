package com.eve.mqtt.client2;

import com.eve.mqtt.client.ClientOptions;
import com.eve.mqtt.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类说明
 * <p>
 *
 * @author 谢洋
 * @version 1.0.0
 * @date 2019/9/6
 */
public class DefaultConnectManager implements ConnectManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectManager.class);

    private volatile static ConcurrentHashMap<String, ConnectPool> connectPools = new ConcurrentHashMap<>();

    private static AtomicInteger channelCount = new AtomicInteger(0);


    private ClientOptions commonOptions;


    private ConnectFactory connectFactory;

    public DefaultConnectManager(ClientOptions commonOptions) {
        this.commonOptions = commonOptions;
        connectFactory = new DefaultConnectFactory();
    }

    @Override
    public void start() {
        connectFactory.init();
    }

    @Override
    public Connection getAndCreateConnection() throws Exception {
        ClientOptions.Node node = ClientUtils.selectServerNode(commonOptions);
        Url url = new Url();
        url.setHostIp(node.getHost());
        url.setPort(node.getPort());
        return getAndCreateConnection(url);
    }

    @Override
    public Connection getAndCreateConnection(Url url) throws Exception {
        Connection connection = connectFactory.createConnection(url);
        String poolKey = url.getHostIp() + ":" + url.getPort();
        ConnectPool connectPool = connectPools.get(poolKey);
        synchronized (this) {
            if (connectPool == null) {
                connectPool = new ConnectPool();
                connectPools.put(poolKey, connectPool);
            }
        }
        channelCount.incrementAndGet();
        return connection;
    }

    @Override
    public void remove(Connection connection) {
        if (null == connection) {
            return;
        }
        channelCount.decrementAndGet();
        String poolKey = connection.getPoolKey();
        ConnectPool connectPool = connectPools.get(poolKey);
        if (connectPool == null) {
            connection.close();
            return;
        }
        connectPool.removeAndTryClose(connection);
    }

}
