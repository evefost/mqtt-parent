package com.eve.mqtt.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 类说明
 * <p>
 *
 * @author 谢洋
 * @version 1.0.0
 * @date 2019/9/6
 */
public class ConnectPool {

    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();

    private Random r = new Random();

    /**
     * add a connection
     *
     * @param connection Connection
     */
    public void add(Connection connection) {
        if (null == connection) {
            return;
        }
        connections.addIfAbsent(connection);

    }

    public void removeAndTryClose(Connection connection) {
        if (null == connection) {
            return;
        }
        connections.remove(connection);
        connection.close();
    }

    public Connection get() {
        if (null != connections) {
            List<Connection> snapshot = new ArrayList<Connection>(connections);
            return snapshot.get(r.nextInt(snapshot.size()));
        } else {
            return null;
        }
    }


    public List<Connection> getAll() {
        return new ArrayList<Connection>(connections);
    }

}
