package com.xhg.mqtt.mq;


import com.xhg.mqtt.mq.message.Message;

/**
 * 消息出入监听
 * @param <M>
 */
public interface MessageOutputListener<M extends Message> {

    /**
     * 消息输出
     */
    void output(M message);
}
