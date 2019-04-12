package com.xie.mqtt.metrics;


import com.xie.mqtt.common.POINT;
import com.xie.mqtt.mq.listener.MessageFailedListener;
import com.xie.mqtt.mq.message.Message;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计mqtt消息出入
 */
@Component
public class MqttFailedListener implements MessageFailedListener,InitializingBean{

    @Autowired
    private MeterRegistry registry;

    private Counter failedCounter;


    @Override
    public void afterPropertiesSet() throws Exception {
        failedCounter = registry.counter("mqtt_output_failed");
    }

    @Override
    public void onFailed(Throwable e, Message message) {
        if(message.getTo().equals(POINT.CLIENT)){
            failedCounter.increment();
        }
    }
}
