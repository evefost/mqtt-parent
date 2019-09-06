package com.eve.mqtt.metrics;

import com.eve.broker.core.listener.MqttListener;
import com.eve.mqtt.mq.SessionManager;
import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统计消息设备信息
 */
@Component
public class MetricsMqtt implements MqttListener<MqttMessage>, SmartInitializingSingleton {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MeterRegistry registry;

    /**
     * 入站信息
     */
    private Counter inputCounter;

    /**
     * 出站信息
     */
    private Counter outputCounter;


    private AtomicDouble totalMessageCount;

    /**
     * 在线客户端
     */
    private AtomicInteger clientOnlineCount;

    /**
     * 客户端上线统计
     */
    private Counter clientConnectCount;

    /**
     * 客户端掉线统计
     */
    private Counter clientDisconnectedCount;

    /**
     * 客户端心跳速率
     */
    private Counter clientPingCount;

    private boolean stopCount=false;

    @Override
    public void input(MqttMessage message) {
        inputCounter.increment();
        if (MqttMessageType.CONNECT == message.fixedHeader().messageType()) {
            clientConnectCount.increment();
        }
        if (MqttMessageType.PINGREQ == message.fixedHeader().messageType()) {
            clientPingCount.increment();
        }
    }

    @Override
    public void output(MqttMessage message) {
        outputCounter.increment();
        if (MqttMessageType.CONNACK == message.fixedHeader().messageType()) {
            clientOnlineCount.set(sessionManager.getOnlineSize());
        }
    }

    @Override
    public void close() {
        clientOnlineCount.set(sessionManager.getOnlineSize());
        clientDisconnectedCount.increment();
    }

    @Override
    public void open() {
        clientOnlineCount.set(sessionManager.getOnlineSize());
        clientConnectCount.increment();
    }


    @Override
    public void afterSingletonsInstantiated() {
        inputCounter = registry.counter(MetricsName.MQTT_MESSAGE_INPUT);
        outputCounter = registry.counter(MetricsName.MQTT_MESSAGE_OUTPUT);
        clientOnlineCount = registry.gauge(MetricsName.MQTT_DEVICE_ONLINE, new AtomicInteger(0));
        clientConnectCount = registry.counter(MetricsName.MQTT_CLIENT_CONNECT);
        clientDisconnectedCount = registry.counter(MetricsName.MQTT_CLIENT_DISCONNECTED);
        clientPingCount = registry.counter(MetricsName.MQTT_CLIENT_PING);
        totalMessageCount = registry.gauge(MetricsName.MQTT_MESSAGE_TOTAL, new AtomicDouble(0d));
        new Thread(new MetresOnlineTask()).start();
    }

    public class MetresOnlineTask implements Runnable {
        @Override
        public void run() {
            while (!stopCount){
                logger.info("更新在线设备统计数[{}]",sessionManager.getOnlineSize());
                clientOnlineCount.set(sessionManager.getOnlineSize());
                totalMessageCount.set(inputCounter.count()+outputCounter.count());
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
