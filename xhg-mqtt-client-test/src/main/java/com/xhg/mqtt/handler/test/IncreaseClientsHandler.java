package com.xhg.mqtt.handler.test;

import static com.xhg.mqtt.netty.MessageClientFactory.getAndCreateChannel;

import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.common.bo.ChangeClientNumber;
import com.xhg.mqtt.handler.AbstractMqttPublishHandler;
import com.xhg.mqtt.netty.MqttNettyClient;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import org.springframework.stereotype.Component;

/**
 * @author xie
 */
@Component
public class IncreaseClientsHandler extends AbstractMqttPublishHandler {

    private volatile static boolean createClientsIng;

    @Override
    public boolean support(Object object) {
        if(object instanceof MqttPublishMessage){
            MqttPublishMessage publishMessage = (MqttPublishMessage) object;
            MqttPublishVariableHeader header = publishMessage.variableHeader();
            String topic = header.topicName();
            if(SystemCmd.TEST_INCREASE_CLIENT.getTopic().equals(topic)){
                return true;
            }
        }
        return false;
    }


    @Override
    protected <T extends MqttMessage> void doProcess(T message) {
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        ChangeClientNumber number = decodeContent(mqttMessage,ChangeClientNumber.class);
        logger.info("收到增客户端命令count:{}",number.getCount());
        increaseClients(number.getCount());
    }


    private  void increaseClients(int count) {
        //创建中，所有请求都丢掉
        if (createClientsIng||count==0) {
            return;
        }
        createClientsIng = true;
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    try {
                        getAndCreateChannel(MqttNettyClient.class);
                    } catch (Exception e) {
                        logger.error("创建连接异常",e);
                    }
                }
                createClientsIng = false;
            }
        }.start();

    }
}
