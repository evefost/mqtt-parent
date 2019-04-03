package com.xhg.mqtt.mq;


import com.xhg.mqtt.mq.handler.AbstractHandler;
import com.xhg.mqtt.mq.handler.up.DeviceConnectHandler;
import com.xhg.mqtt.mq.handler.up.DeviceDefaultHandler;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 消息处理器
 *
 * @author xie
 */
@Component
public class HandlerManager implements SmartInitializingSingleton, ApplicationContextAware {

    protected static Logger logger = LoggerFactory.getLogger(HandlerManager.class);

    private final static List<AbstractHandler> handlers = new ArrayList<>();

    private  static AbstractHandler defaultHandler;

    private ApplicationContext applicationContext;


    public final static void process(Message message) {
        boolean matchHandler = false;
        for (AbstractHandler handler : handlers) {
            if (handler.support(message)) {
                matchHandler = true;
                handler.processMessage(message);
            }
        }
        if (!matchHandler) {
            if(message instanceof MqttWrapperMessage){
                defaultHandler.processMessage(message);
            }else {
                logger.warn("消息没配置到处理器");
            }
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, AbstractHandler> beansOfType = applicationContext.getBeansOfType(AbstractHandler.class);
        beansOfType.forEach(new BiConsumer<String, AbstractHandler>() {
            @Override
            public void accept(String key, AbstractHandler bean) {
                if(bean instanceof DeviceDefaultHandler){
                    defaultHandler= bean;
                }else {
                    handlers.add(bean);
                }
            }
        });
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
