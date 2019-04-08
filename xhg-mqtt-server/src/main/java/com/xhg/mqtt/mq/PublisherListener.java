package com.xhg.mqtt.mq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xhg.mqtt.common.POINT;
import com.xhg.mqtt.common.handler.HandlerDispatcher;
import com.xhg.mqtt.common.proto.MqttMessagePb;
import com.xhg.mqtt.common.proto.MqttMessagePb.MqttHead;
import com.xhg.mqtt.mq.handler.up.DeviceConnectHandler;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xie
 */
public class PublisherListener extends AbstractInterceptHandler implements Decoder<MqttWrapperMessage> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getID() {
        return "MqttBrokerApplicationPublishListener";
    }


    @Autowired
    private DeviceConnectHandler deviceConnectHandler;

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        String clientID = msg.getClientID();
    }


    @Override
    public void onConnect(InterceptConnectMessage mqttMessage) {
        String clientID = mqttMessage.getClientID();
        logger.debug("收到[{}]连接消息", clientID);
        MqttWrapperMessage message = new MqttWrapperMessage();
        message.setSrcMessage(mqttMessage.getMsg());
        message.setFrom(POINT.CLIENT);
        message.setTo(POINT.SERVER);
        message.setClientId(clientID);
        HandlerDispatcher.process(message);
    }

    @Override
    public void onPublish(InterceptPublishMessage mqttMessage) {
        String clientID = mqttMessage.getClientID();
        String topicName = mqttMessage.getTopicName();
        logger.debug("收到[{}]的[{}]消息", clientID, topicName);
        MqttWrapperMessage message = new MqttWrapperMessage();
        message.setClientId(clientID);
        message.setSrcMessage(mqttMessage.getMsg());
        message.setTopic(topicName);
        message.setFrom(POINT.CLIENT);
        message.setTo(POINT.SERVER);
        ByteBuf byteBuf = mqttMessage.getPayload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
        message.setMqttPayload(payload);
        decode(message);
        HandlerDispatcher.process(message);
    }

    @Override
    public void decode(MqttWrapperMessage message) {
        MqttMessage srcMessage = message.getSrcMessage();
        try {
            MqttMessagePb.MqttMessage mqttMessage = MqttMessagePb.MqttMessage.parseFrom(message.getMqttPayload());
            message.setBuzMessage(mqttMessage);
            if (logger.isDebugEnabled()) {
                MqttHead head = mqttMessage.getHead();
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("解码设备消息失败topic:{}/msgId:{}", message.getTopic(), e);
        } catch (Throwable e) {
            logger.error("解码设备消息失败topic:{}/msgId:{}", message.getTopic(), e);
        }
    }
}