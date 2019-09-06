package com.eve.mqtt.mq;


import com.eve.mqtt.common.handler.Handler;
import com.eve.mqtt.common.handler.HandlerDispatcher;
import com.eve.mqtt.mq.client.AbstractMessageClient;
import com.eve.mqtt.mq.handler.DeviceConnectHandler;
import com.eve.mqtt.mq.listener.MessageFailedListener;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MqttHandlerAutoConfig implements SmartInitializingSingleton {


    @Autowired
    private List<MessageFailedListener> failedListeners;

    @Autowired
    private List<Handler> handlers;


    @Bean
    DeviceConnectHandler deviceConnectHandler() {
        return new DeviceConnectHandler();
    }



    @Override
    public void afterSingletonsInstantiated() {
        //注册消息监听器
        for (MessageFailedListener listener : failedListeners) {
            AbstractMessageClient.registerFailedListner(listener);
        }
        HandlerDispatcher.addAllHandler(handlers);

    }


}
