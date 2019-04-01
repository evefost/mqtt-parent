package com.xhg.mqtt.mq.handler.up;


;
import static com.xhg.mqtt.mq.POINT.MQTT;

import com.xhg.mqtt.mq.POINT;
import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.client.XhgMqttClient;
import com.xhg.mqtt.mq.handler.AbstractHandler;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttHead;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage.Builder;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;

/**
 * 终端登录处理
 * @author xie
 */
public abstract class AbstractUpHandler extends AbstractHandler<MqttWrapperMessage> {

    @Resource(name = "mqttClient")
    private XhgMqttClient xhgMqttClient;

    @Value("${product-key:}")
    private String productKey;

    public AbstractUpHandler(MessageClient client) {
        super(client);
    }

    @Override
    protected void doAck(MqttWrapperMessage message) {

        MqttMessage srcMsg = message.getMqttMessage();
        MqttHead srcHead = srcMsg.getHead();
        Builder messageBuilder = MqttMessage.newBuilder();
        MqttHead.Builder headBuilder = MqttHead.newBuilder();
        headBuilder.setMessageId(srcHead.getMessageId());
        headBuilder.setDeviceId(srcHead.getDeviceId());
        headBuilder.setEventCode(srcHead.getEventCode());
        headBuilder.setCc(0);

        messageBuilder.setHead(headBuilder);
        MqttMessage replyMqtt = messageBuilder.build();
        byte[] payload = replyMqtt.toByteArray();
        String deviceId = srcMsg.getHead().getDeviceId();
        String topic = productKey+"/device/"+deviceId+"/ack";
        if(logger.isDebugEnabled()){
            logger.debug("回应设备请求eventCode[{}] messageId[{}] ",srcHead.getEventCode(),srcMsg.getHead().getMessageId());
        }
        Message<org.eclipse.paho.client.mqttv3.MqttMessage> replyMessage = new Message<>();
        replyMessage.setTopic(topic);
        replyMessage.setMqttMessage(replyMqtt);
        replyMessage.setFrom(POINT.SERVER);
        replyMessage.setTo(MQTT);
        replyMessage.setMqttPayload(payload);
        xhgMqttClient.publish(replyMessage);
    }


    @Override
    public POINT getPoint() {
        return MQTT;
    }


}
