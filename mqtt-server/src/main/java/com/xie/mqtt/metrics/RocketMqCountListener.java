package com.xie.mqtt.metrics;


import com.xie.mqtt.mq.listener.MessageInputListener;
import com.xie.mqtt.mq.listener.MessageOutputListener;
import com.xie.mqtt.mq.message.Message;
import com.xie.mqtt.mq.message.RocketWrapperMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计rocket mq消息出入
 */
@Component
public class RocketMqCountListener implements MessageInputListener<Message>, MessageOutputListener<Message>,
    InitializingBean {

    @Autowired
    private MeterRegistry registry;

    private Counter inputCounter;

    private Counter outputCounter;

    @Override
    public void input(Message message) {
        if(message instanceof RocketWrapperMessage){
            inputCounter.increment();
        }
    }

    @Override
    public void output(Message message) {
        if(message instanceof RocketWrapperMessage){
            inputCounter.increment();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        inputCounter = registry.counter("roketmq_message_input");
        outputCounter = registry.counter("roketmq_message_output");
    }
}
