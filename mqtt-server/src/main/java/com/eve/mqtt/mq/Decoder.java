package com.eve.mqtt.mq;


import com.eve.mqtt.mq.message.Message;

public interface Decoder<M extends Message> {

    /**
     * 解确消息
     * @param message
     */
    void decode(M message);

}
