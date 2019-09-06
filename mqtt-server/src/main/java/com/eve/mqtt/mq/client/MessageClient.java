package com.eve.mqtt.mq.client;


import com.eve.mqtt.mq.message.Message;

public interface MessageClient {



     <M extends Message> void  publish(M message);

     String getClientId();


}
