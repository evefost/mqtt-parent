package com.xhg.mqtt.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;


public abstract class AbstractMqttPublishHandler extends AbstractHandler<MqttPublishMessage>  {

    @Override
    public <O> O decodeContent(MqttPublishMessage message, Class<O> outputClass) {
        ByteBuf byteBuf =  message.payload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
        O content = JSON.parseObject(new String(payload), outputClass);
        return content;
    }



}
