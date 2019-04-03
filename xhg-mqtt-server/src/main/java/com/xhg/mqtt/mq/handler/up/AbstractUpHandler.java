package com.xhg.mqtt.mq.handler.up;


import com.xhg.mqtt.common.POINT;
import com.xhg.mqtt.common.proto.MqttMessagePb;
import com.xhg.mqtt.common.proto.MqttMessagePb.MqttHead;
import com.xhg.mqtt.common.proto.MqttMessagePb.MqttMessage;
import com.xhg.mqtt.common.proto.MqttMessagePb.MqttMessage.Builder;
import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.client.XhgMqttClient;
import com.xhg.mqtt.mq.handler.AbstractHandler;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
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
    protected void doAck(Message message) {

        MqttMessagePb.MqttMessage srcMsg = message.getBuzMessage();
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
        Message<io.netty.handler.codec.mqtt.MqttMessage> replyMessage = new Message<>();
        replyMessage.setClientId(message.getClientId());
        replyMessage.setTopic(topic);
        replyMessage.setBuzMessage(replyMqtt);
        replyMessage.setFrom(POINT.SERVER);
        replyMessage.setTo(POINT.CLIENT);
        replyMessage.setMqttPayload(payload);
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(topic)
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(payload)).build();
        replyMessage.setSrcMessage(publish);
        xhgMqttClient.publish(replyMessage);
    }


    @Override
    public POINT getPoint() {
        return POINT.CLIENT;
    }


}
