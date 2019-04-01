package com.xhg.mqtt.mq.handler;


import com.xhg.mqtt.mq.POINT;
import com.xhg.mqtt.mq.message.Message;

public interface Handler {

    boolean support(Message message);

    String getEventCode();

    POINT getPoint();


}
