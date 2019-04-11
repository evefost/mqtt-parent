package com.xhg.mqtt.common.handler;

import com.alibaba.fastjson.JSON;
import com.xhg.mqtt.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;


/**
 * @author xieyang
 */
public abstract class AbstractMqttPublishHandler extends AbstractHandler<MqttPublishMessage> {


    @Override
    public boolean support(Object object) {
        if (object instanceof MqttPublishMessage) {
            String topic = getTopic((MqttPublishMessage) object);
            if (topic.startsWith(Constants.SYSTEM_CONTROL_PATTERN)) {
                return true;
            }
        }
        return false;
    }

    protected String getTopic( MqttPublishMessage publishMessage){
        MqttPublishVariableHeader header = publishMessage.variableHeader();
        return header.topicName();
    }

    @Override
    public <O> O decodeContent(MqttPublishMessage message, Class<O> outputClass) {
        ByteBuf byteBuf =  message.payload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
        O content = JSON.parseObject(new String(payload), outputClass);
        return content;
    }

}
