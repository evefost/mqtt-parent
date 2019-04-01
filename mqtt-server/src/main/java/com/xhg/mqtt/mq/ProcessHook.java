package com.xhg.mqtt.mq;


import com.xhg.mqtt.mq.message.Message;

/**
 * 处理勾子
 * @param <M>
 */
public interface ProcessHook<M extends Message> {

    /**
     * 处理前
     * @param message
     */
    void beforeProcess(M message);

    /**
     * 处量后
     * @param message
     */
    void afterProcess(M message);
}
