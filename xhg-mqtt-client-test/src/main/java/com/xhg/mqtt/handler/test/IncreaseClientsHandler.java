package com.xhg.mqtt.handler.test;

import static com.xhg.mqtt.netty.MessageClientFactory.getAndCreateChannel;
import static com.xhg.mqtt.netty.MessageClientFactory.getCommonOptoins;

import com.alibaba.fastjson.JSON;
import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.common.bo.IncreaseClient;
import com.xhg.mqtt.handler.AbstractHandler;
import com.xhg.mqtt.netty.ClientOptions;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import org.springframework.stereotype.Component;

@Component
public class IncreaseClientsHandler extends AbstractHandler {

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
        ByteBuf byteBuf = mqttMessage.payload();
        byte[] payload = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(payload);
        IncreaseClient increaseClient = JSON.parseObject(new String(payload), IncreaseClient.class);
        logger.info("收到增客户端命令count:{}",increaseClient.getCount());
        increaseClients(increaseClient.getCount());
    }


    private  void increaseClients(int count) {
        //创建中，所有请求都丢掉
        if (createClientsIng) {
            return;
        }
        createClientsIng = true;
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    try {
                        ClientOptions clone = getCommonOptoins().clone();
                        getAndCreateChannel(clone);
                    } catch (Exception e) {
                        logger.error("创建连接异常",e);
                    }
                }
                createClientsIng = false;
            }
        }.start();

    }
}
