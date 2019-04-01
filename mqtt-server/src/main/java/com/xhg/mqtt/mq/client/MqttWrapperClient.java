package com.xhg.mqtt.mq.client;

import com.xhg.mqtt.mq.message.Message;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xie
 */
 class MqttWrapperClient implements MessageClient {

     protected Logger logger = LoggerFactory.getLogger(getClass());

     private IMqttClient iMqttClient;

     private boolean consumer;

     public MqttWrapperClient(IMqttClient iMqttClient,boolean consumer){
          this.iMqttClient=iMqttClient;
          this.consumer = consumer;
     }

     @Override
     public <M extends Message> void publish(M message) {
          String topic = message.getTopic();
          byte[] payload = message.getMqttPayload();
          try {
               iMqttClient.publish(topic, payload, 0, false);
          } catch (Throwable e) {
               throw new RuntimeException(e);
          }
     }
    @Override
    public String getClientId() {
        return iMqttClient.getClientId();
    }
}
