package com.xhg.mqtt.handler.test;

import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.handler.AbstractHandler;
import com.xhg.mqtt.netty.MessageClientFactory;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import org.springframework.stereotype.Component;

@Component
public class DisconnectClientHandler extends AbstractHandler {

    @Override
    public boolean support(Object object) {
        if(object instanceof MqttPublishMessage){
            MqttPublishMessage publishMessage = (MqttPublishMessage) object;
            MqttPublishVariableHeader header = publishMessage.variableHeader();
            String topic = header.topicName();
            if(SystemCmd.TEST_DISCONNECT_CLIENT.getTopic().equals(topic)){
                return true;
            }
        }
        return false;
    }


    @Override
    protected <T extends MqttMessage> void doProcess(T message) {
        MessageClientFactory.closeAll();
    }
}
