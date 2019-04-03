package com.xhg.mqtt.config;


import com.xhg.mqtt.common.handler.Handler;
import java.util.Map;
import java.util.function.BiConsumer;

import com.xhg.mqtt.common.handler.HandlerDispatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttClientAutoConfig implements SmartInitializingSingleton, ApplicationContextAware {

    private ApplicationContext applicationContext;



    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Handler> beansOfType = applicationContext.getBeansOfType(Handler.class);
        beansOfType.forEach(new BiConsumer<String, Handler>() {
            @Override
            public void accept(String key, Handler bean) {
                HandlerDispatcher.addHandler(bean);
            }
        });
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }






}
