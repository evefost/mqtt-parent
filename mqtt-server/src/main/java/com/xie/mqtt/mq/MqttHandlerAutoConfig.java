package com.xie.mqtt.mq;


import com.xie.mqtt.common.handler.Handler;
import com.xie.mqtt.common.handler.HandlerDispatcher;
import com.xie.mqtt.mq.client.AbstractMessageClient;
import com.xie.mqtt.mq.handler.DeviceConnectHandler;
import com.xie.mqtt.mq.listener.MessageFailedListener;
import java.util.List;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
