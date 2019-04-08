package com.xhg.mqtt.common.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;


/**
 * @author xieyang
 */
public abstract class AbstractMqttPublishHandler extends AbstractHandler<MqttPublishMessage> {



    @Override
    public <O> O decodeContent(MqttPublishMessage message, Class<O> outputClass) {
        ByteBuf byteBuf =  message.payload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
        O content = JSON.parseObject(new String(payload), outputClass);
        return content;
    }

}
