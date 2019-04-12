package com.xie.mqtt.mq.listener;


/**
 * 消息出入监听
 * @param <M>
 */
public interface MessageInputListener<M> {


    /**
     * 消息输入
     */
    void input(M message);

}
