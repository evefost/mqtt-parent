package com.xhg.mqtt.mq.client;


import com.xhg.mqtt.mq.message.Message;

public interface MessageClient {



     <M extends Message> void  publish(M message);

     String getClientId();


}
