package com.eve.mqtt.common.handler;


import com.eve.mqtt.common.POINT;

/**
 * 消息处理器顶层接口
 * @author xie
 * @param <M>
 */
public interface Handler<M> {

    boolean support(Object message);

    void processMessage(M message);

    POINT getPoint();


}
