package com.xhg.mqtt.mq.listener;


import com.xhg.mqtt.mq.message.Message;

/**
 * 消息失败监听
 * @param <M>
 */
public interface MessageFailedListener<M extends Message> {


    /**
     * 消息失败监听
     * @param e
     * @param message
     */
    void onFailed(Throwable e, M message);
}
