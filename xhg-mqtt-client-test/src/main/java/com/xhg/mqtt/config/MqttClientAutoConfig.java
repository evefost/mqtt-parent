package com.xhg.mqtt.config;


import com.xhg.mqtt.common.handler.Handler;
import com.xhg.mqtt.common.handler.HandlerDispatcher;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttClientAutoConfig implements SmartInitializingSingleton, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private List<Handler> handlers;

    @Override
    public void afterSingletonsInstantiated() {
        HandlerDispatcher.addAllHandler(handlers);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }






}
