package com.xie.mqtt.mq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xie.mqtt.common.POINT;
import com.xie.mqtt.common.handler.HandlerDispatcher;
import com.xie.mqtt.common.proto.MqttMessagePb;
import com.xie.mqtt.common.proto.MqttMessagePb.MqttHead;
import com.xie.mqtt.mq.message.MqttWrapperMessage;
import com.eve.broker.interception.AbstractInterceptHandler;
import com.eve.broker.interception.messages.InterceptConnectMessage;
import com.eve.broker.interception.messages.InterceptDisconnectMessage;
import com.eve.broker.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xie
 */
public class PublisherListener extends AbstractInterceptHandler implements Decoder<MqttWrapperMessage> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getID() {
        return "MqttBrokerApplicationPublishListener";
    }


    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        String clientID = msg.getClientID();
    }


    @Override
    public void onConnect(InterceptConnectMessage mqttMessage) {
        String clientID = mqttMessage.getClientID();
        logger.debug("收到[{}]连接消息", clientID);
        MqttMessage msg = mqttMessage.getMsg();
        HandlerDispatcher.process(msg);
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
       // decode(message);
        //HandlerDispatcher.process(message);
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