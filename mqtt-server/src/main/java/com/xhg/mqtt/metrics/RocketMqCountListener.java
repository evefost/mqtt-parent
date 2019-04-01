package com.xhg.mqtt.metrics;


import com.xhg.mqtt.mq.MessageInputListener;
import com.xhg.mqtt.mq.MessageOutputListener;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.RocketWrapperMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计rocket mq消息出入
 */
@Component
public class RocketMqCountListener implements MessageInputListener,MessageOutputListener,InitializingBean {

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
