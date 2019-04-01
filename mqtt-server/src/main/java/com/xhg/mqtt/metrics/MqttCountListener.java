package com.xhg.mqtt.metrics;


import com.xhg.mqtt.mq.MessageInputListener;
import com.xhg.mqtt.mq.MessageOutputListener;
import com.xhg.mqtt.mq.POINT;
import com.xhg.mqtt.mq.SessionManager;
import com.xhg.mqtt.mq.message.Message;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计mqtt消息出入
 */
@Component
public class MqttCountListener implements MessageInputListener,MessageOutputListener,InitializingBean{

    @Autowired
    private MeterRegistry registry;

    private Counter inputCounter;

    private Counter outputCounter;

    private AtomicInteger channelCount;

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void input(Message message) {
        if(message.getFrom().equals(POINT.MQTT)){
            inputCounter.increment();
            calculateChannelMessageRate();
        }
    }

    @Override
    public void output(Message message) {
        if(message.getTo().equals(POINT.MQTT)){
            outputCounter.increment();
            calculateChannelMessageRate();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        inputCounter = registry.counter("mqtt_message_input");
        outputCounter = registry.counter("mqtt_message_output");
        channelCount = registry.gauge("server_channel_rate", new AtomicInteger(0));
    }

    private volatile int statisticsWindown = 1000;

    private volatile long lastCalculateTime = System.currentTimeMillis();

    private volatile double lastValue=0;


    private void calculateChannelMessageRate(){
        long currentT = System.currentTimeMillis();
        if(currentT-statisticsWindown>lastCalculateTime){
            int clientSize = sessionManager.getSessionRegistry().getSessions().size();
            double current = inputCounter.count() + outputCounter.count();
            double v = (current - lastValue) / (statisticsWindown/1000*clientSize);
            channelCount.set((int) v);
            lastCalculateTime = currentT;
            lastValue=current;
        }

    }



}
