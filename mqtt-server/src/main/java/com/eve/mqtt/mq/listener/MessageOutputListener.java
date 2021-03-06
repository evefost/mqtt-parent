package com.eve.mqtt.mq.listener;


/**
 * 消息出入监听
 * @param <M>
 */
public interface MessageOutputListener<M> {

    /**
     * 消息输出
     */
    void output(M message);
}
