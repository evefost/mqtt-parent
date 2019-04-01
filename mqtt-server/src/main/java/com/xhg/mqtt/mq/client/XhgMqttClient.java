package com.xhg.mqtt.mq.client;


import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.RocketWrapperMessage;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component("mqttClient")
public class XhgMqttClient extends AbstractMessageClient<RocketWrapperMessage> {

    protected Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    protected void doPublish(Message message) {
        String topic = message.getTopic();
        MqttMessage mqttMessage = message.getMqttMessage();
        MessageClient iMqttClient= null;
        try {
            iMqttClient = choseClient();
            iMqttClient.publish(message);
        } catch (Throwable e) {
            logger.error("serverId[{}] 发送失败topic:[{}]  to clientId[{}] 失败 ",iMqttClient.getClientId(),topic,mqttMessage.getHead().getDeviceId(),e);
            onFailed(e,message);
        }
    }

    @Override
    protected MessageClient choseClient() {
        return ClientFactory.getClient(false);
    }


}
