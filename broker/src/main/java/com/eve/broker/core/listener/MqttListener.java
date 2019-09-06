package com.eve.broker.core.listener;

/**
 * @author xie
 */
public interface MqttListener<M> {

    /**
     * 入站消息
     */
    void input(M message);

    /**
     * 出站消息
     * @param message
     */
    void output(M message);

    void close();

    void open();
}
