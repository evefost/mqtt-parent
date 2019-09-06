package com.eve.mqtt.common;



/**
 * 处理勾子
 * @param <M>
 */
public interface ProcessHook<M> {

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
