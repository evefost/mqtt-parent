package com.eve.mqtt.client;

/**
 * 类说明
 * <p>
 *
 * @author 谢洋
 * @version 1.0.0
 * @date 2019/9/6
 */
public interface ConnectManager {

    void start();

    Connection getAndCreateConnection() throws Exception;

    Connection getAndCreateConnection(Url url) throws Exception;

    void remove(Connection connection);


}
