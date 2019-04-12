package com.xie.mqtt.mq.client;


import com.xie.mqtt.mq.message.Message;

public interface MessageClient {



     <M extends Message> void  publish(M message);

     String getClientId();


}
