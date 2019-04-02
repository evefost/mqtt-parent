package com.xhg.mqtt.metrics;

import com.xhg.mqtt.mq.SessionManager;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.moquette.broker.listener.MqttListener;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计消息设备信息
 */
@Component
public class MetricsMqttListener implements MqttListener<MqttMessage>, SmartInitializingSingleton {
    private Logger logger = LoggerFactory.getLogger(getClass());

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

    private final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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

    private boolean stopCount=false;

    @Override
    public void afterSingletonsInstantiated() {
        inputCounter = registry.counter("mqtt_message_input");
        outputCounter = registry.counter("mqtt_message_output");
        deviceOnlineCount = registry.gauge("mqtt_device_online", new AtomicInteger(0));
        channelCount = registry.gauge("mqtt_channel_rate", new AtomicInteger(0));
        new Thread(new MetresOnlineTask()).start();
    }

    public class MetresOnlineTask implements Runnable {

        @Override
        public void run() {
            while (!stopCount){
                logger.info("更新在线设备统计数[{}]",sessionManager.getOnlineSize());
                deviceOnlineCount.set(sessionManager.getOnlineSize());
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @PreDestroy
    public void onDestroy(){
        stopCount = true;
    }
}
