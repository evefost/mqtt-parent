package com.xhg.mqtt.handler;


/**
 * 消息处理器顶层接口
 * @author xie
 * @param <M>
 */
public interface Handler<M> {

    boolean support(Object object);

    void processMessage(M message);




}
