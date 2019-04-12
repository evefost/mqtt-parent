package com.xhg.mqtt.mq;


import com.xhg.mqtt.mq.message.Message;

public interface Decoder<M extends Message> {

    /**
     * 解确消息
     * @param message
     */
    void decode(M message);

}
