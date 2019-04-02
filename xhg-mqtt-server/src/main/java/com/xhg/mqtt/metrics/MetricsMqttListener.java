package com.xhg.mqtt.metrics;

import com.xhg.mqtt.mq.SessionManager;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.moquette.broker.listener.MqttListener;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计消息设备信息
 */
@Component
public class MetricsMqttListener implements MqttListener<MqttMessage>, InitializingBean {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MeterRegistry registry;

    private AtomicInteger deviceOnlineCount;

    private Counter inputCounter;

    private Counter outputCounter;

    private AtomicInteger channelCount;

    private volatile int statisticsWindown = 1000;

    private volatile long lastCalculateTime = System.currentTimeMillis();

    private volatile double lastValue = 0;

    @Override
    public void input(MqttMessage message) {
        inputCounter.increment();
        calculateChannelMessageRate();
    }

    @Override
    public void output(MqttMessage message) {
        outputCounter.increment();
        calculateChannelMessageRate();
        if (MqttMessageType.CONNACK == message.fixedHeader().messageType()) {
            deviceOnlineCount.set(sessionManager.getOnlineSize());
        }
    }

    @Override
    public void close() {
        deviceOnlineCount.set(sessionManager.getOnlineSize());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        inputCounter = registry.counter("mqtt_message_input");
        outputCounter = registry.counter("mqtt_message_output");
        deviceOnlineCount = registry.gauge("mqtt_device_online", new AtomicInteger(0));
        channelCount = registry.gauge("mqtt_channel_rate", new AtomicInteger(0));
    }

    private void calculateChannelMessageRate() {
        long currentT = System.currentTimeMillis();
        if (currentT - statisticsWindown > lastCalculateTime) {
            int clientSize = sessionManager.getSessionRegistry().getSessions().size();
            double current = inputCounter.count() + outputCounter.count();
            double v = (current - lastValue) / (statisticsWindown / 1000 * clientSize);
            channelCount.set((int) v);
            lastCalculateTime = currentT;
            lastValue = current;
            deviceOnlineCount.set(sessionManager.getOnlineSize());
        }

    }
}
