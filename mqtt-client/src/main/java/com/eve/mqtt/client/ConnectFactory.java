package com.eve.mqtt.client;

/**
 * <p>
 *
 * @author xieyang
 * @version 1.0.0
 * @date 2019/9/6
 */
public interface ConnectFactory {

    /**
     *
     */
    void init();

    /**
     * createConnection by default connection type
     *
     * @param url
     * @return
     * @throws Exception
     */
    Connection createConnection(Url url) throws Exception;

    /**
     * createConnection by specify connection type
     *
     * @param url
     * @param connectClass
     * @return
     * @throws Exception
     */
    Connection createConnection(Url url, Class<? extends Connection> connectClass) throws Exception;

}
