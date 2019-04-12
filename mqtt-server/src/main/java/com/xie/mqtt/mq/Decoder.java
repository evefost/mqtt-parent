package com.xie.mqtt.mq;


import com.xie.mqtt.mq.message.Message;

public interface Decoder<M extends Message> {

    /**
     * 解确消息
     * @param message
     */
    void decode(M message);

}
