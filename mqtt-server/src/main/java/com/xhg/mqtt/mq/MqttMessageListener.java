package com.xhg.mqtt.mq;


import com.google.protobuf.InvalidProtocolBufferException;

import com.xhg.mqtt.mq.message.MqttWrapperMessage;
import com.xhg.mqtt.mq.proto.MqttMessagePb;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttHead;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MqttMessageListener implements MqttCallbackExtended, Decoder<MqttWrapperMessage> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private MqttClient client;
    private String[] topics;
    private boolean consumer;


    public MqttMessageListener(MqttClient client,String[] topics,boolean consumer) {
        this.client = client;
        this.topics = topics;
        this.consumer = consumer;
    }

    public void setClient(MqttClient client){
        this.client = client;
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        logger.info("mqtt client [{}]成功连接:[{}] 是否为重连[{}]",client.getClientId(),serverURI,reconnect);
        if(!consumer){
            return;
        }
        try {
            client.subscribe(topics);
        } catch (MqttException e) {
            logger.error("mqtt client[{}]订阅失败:[{}]",client.getClientId(), topics,e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.error("mqtt[{}]连接断开:", client.getClientId(),cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.debug("mqtt 收到消息 topic: {}", topic);
        MqttWrapperMessage msg = new MqttWrapperMessage();
        msg.setSrcMessage(message);
        msg.setFrom(POINT.MQTT);
        msg.setTo(POINT.SERVER);
        msg.setTopic(topic);
        decode(msg);
        HandlerManager.process(msg);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


    @Override
    public void decode(MqttWrapperMessage message) {
        if(logger.isDebugEnabled()){
            logger.debug("解码设备主题消息:{}", message.getTopic());
        }
        MqttMessage srcMessage = message.getSrcMessage();
        message.setMqttPayload(srcMessage.getPayload());
        try {
            MqttMessagePb.MqttMessage mqttMessage = MqttMessagePb.MqttMessage.parseFrom(message.getMqttPayload());
            message.setMqttMessage(mqttMessage);
            if(logger.isDebugEnabled()){
                MqttHead head = mqttMessage.getHead();
                logger.debug("解码设备消息 deviceId:[{}]  eventCode:[{}] cc[{}] msgId:[{}] ",head.getDeviceId(),head.getEventCode(),head.getCc(), head.getMessageId());
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("解码设备消息失败topic:{}/msgId:{}", message.getTopic(),srcMessage.getId(), e);
        }catch (Throwable e){
            logger.error("解码设备消息失败topic:{}/msgId:{}", message.getTopic(),srcMessage.getId(), e);
        }
    }


}
