package com.eve.mqtt.handler;

import com.eve.mqtt.common.POINT;
import com.eve.mqtt.common.handler.AbstractMqttPublishHandler;
import com.eve.mqtt.common.proto.MqttMessagePb;
import com.google.protobuf.InvalidProtocolBufferException;
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
        MqttMessagePb.MqttMessage decode = decodeHeader((MqttPublishMessage) object);
        MqttMessagePb.MqttHead head = decode.getHead();
        if( getEventCode().equals(head.getEventCode())){
            return true;
        }
        return false;
    }

    protected MqttMessagePb.MqttMessage decodeHeader( MqttPublishMessage message) {
        ByteBuf byteBuf = message.payload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
        MqttMessagePb.MqttMessage customerMessage = null;
        try {
            customerMessage = MqttMessagePb.MqttMessage.parseFrom(payload);
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
