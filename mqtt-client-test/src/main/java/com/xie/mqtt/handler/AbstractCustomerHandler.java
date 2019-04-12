package com.xie.mqtt.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xie.mqtt.common.POINT;
import com.xie.mqtt.common.handler.AbstractMqttPublishHandler;
import com.xie.mqtt.common.proto.MqttMessagePb;
import com.xie.mqtt.common.proto.MqttMessagePb.MqttHead;
import com.xie.mqtt.common.proto.MqttMessagePb.MqttMessage;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 *
 * @author xie
 */
public abstract class AbstractCustomerHandler extends AbstractMqttPublishHandler {

    @Override
    public boolean support(Object object) {
        if(super.support(object)){
            return false;
        }
        MqttMessage decode = decodeHeader((MqttPublishMessage) object);
        MqttHead head = decode.getHead();
        if( getEventCode().equals(head.getEventCode())){
            return true;
        }
        return false;
    }

    protected MqttMessagePb.MqttMessage decodeHeader( MqttPublishMessage message) {
        ByteBuf byteBuf = message.payload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
        MqttMessage customerMessage = null;
        try {
            customerMessage = MqttMessage.parseFrom(payload);
        } catch (InvalidProtocolBufferException e) {
            logger.error("解码设备消息失败topic:{}/msgId:{}", getTopic(message), e);
        }
        return customerMessage;
    }

    /**
     * ssss
     * @return
     */
    protected abstract String getEventCode();

    @Override
    public POINT getPoint() {
        return POINT.SERVER;
    }

}
