package com.xhg.mqtt.mq;


import com.xhg.mqtt.mq.message.Message;

/**
 * 消息出入监听
 * @param <M>
 */
public interface MessageInputListener<M extends Message> {


    /**
     * 消息输入
     */
    void input(M message);

}
