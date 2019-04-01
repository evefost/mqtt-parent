package com.xhg.mqtt.mq.client;

import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;
import com.xhg.mqtt.mq.message.RocketMqBaseMessage;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xie
 */
@Component("rocketMqClient")
public class XhgRocketMqClient extends AbstractMessageClient<MqttWrapperMessage> {


    protected Logger logger = LoggerFactory.getLogger(getClass());

//    @Autowired(required = false)
//    private MessagePublisher messagePublisher;

    @Override
    protected void doPublish(Message message) {
        byte[] payload = message.getMqttPayload();
        String data = Base64.encodeBase64String(payload);
        RocketMqBaseMessage rocketMqBaseMessage = new RocketMqBaseMessage();
        rocketMqBaseMessage.setData(data);
//        SourceEvent<RocketMqBaseMessage> event = new SourceEvent<>(this);
//        event.setTopic(message.getTopic());
//        event.setData(rocketMqBaseMessage);
//        choseClient().publishEvent(event, new SendMsgCallback() {
//            @Override
//            public void onSuccess(SendMsgResult sendResult) {
//
//            }
//
//            @Override
//            public void onException(Throwable e) {
//                onFailed(e,message);
//            }
//        });
    }

    @Override
    protected Object choseClient() {
        return null;
    }
}
