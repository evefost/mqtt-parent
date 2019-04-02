package com.xhg.mqtt.common.handler;


import com.xhg.mqtt.common.POINT;

/**
 * 消息处理器顶层接口
 * @author xie
 * @param <M>
 */
public interface Handler<M> {

    boolean support(M message);


    POINT getPoint();


}
