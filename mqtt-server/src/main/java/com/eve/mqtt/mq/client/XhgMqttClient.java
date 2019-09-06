package com.eve.mqtt.mq.client;


import com.eve.broker.core.Session;
import com.eve.broker.core.subscriptions.Topic;
import com.eve.mqtt.mq.SessionManager;
import com.eve.mqtt.mq.message.Message;
import com.eve.mqtt.mq.message.RocketWrapperMessage;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("mqttClient")
public class XhgMqttClient extends AbstractMessageClient<RocketWrapperMessage> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SessionManager sessionManager;

    @Override
    protected void doPublish(Message message) {
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message.getSrcMessage();
        ByteBuf payload = mqttMessage.payload();
        Session client = sessionManager.getSession(message.getClientId());
        Topic destinationTopic = new Topic(message.getTopic());
        try {
            client.sendPublishOnSessionAtQos(destinationTopic, mqttMessage.fixedHeader().qosLevel(), payload);
        } catch (Throwable e) {
            logger.error("发送失败topic:[{}]  to clientId[{}] 失败 ", message.getTopic(), message.getClientId(), e);
            onFailed(e,message);
        }
    }

    @Override
    protected MessageClient choseClient() {
        return null;
    }


}
