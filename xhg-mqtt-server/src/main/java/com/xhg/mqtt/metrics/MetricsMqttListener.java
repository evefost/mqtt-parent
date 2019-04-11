package com.xhg.mqtt.metrics;

import static com.xhg.mqtt.metrics.MetricsName.MQTT_CLIENT_CONNECT;
import static com.xhg.mqtt.metrics.MetricsName.MQTT_CLIENT_DISCONNECTED;
import static com.xhg.mqtt.metrics.MetricsName.MQTT_CLIENT_PING;
import static com.xhg.mqtt.metrics.MetricsName.MQTT_DEVICE_ONLINE;
import static com.xhg.mqtt.metrics.MetricsName.MQTT_MESSAGE_INPUT;
import static com.xhg.mqtt.metrics.MetricsName.MQTT_MESSAGE_OUTPUT;

import com.xhg.mqtt.mq.SessionManager;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.moquette.broker.listener.MqttListener;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
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

    /**
     * 入站信息
     */
    private Counter inputCounter;

    /**
     * 出站信息
     */
    private Counter outputCounter;

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
        inputCounter = registry.counter(MQTT_MESSAGE_INPUT);
        outputCounter = registry.counter(MQTT_MESSAGE_OUTPUT);
        clientOnlineCount = registry.gauge(MQTT_DEVICE_ONLINE, new AtomicInteger(0));
        clientConnectCount = registry.counter(MQTT_CLIENT_CONNECT);
        clientDisconnectedCount = registry.counter(MQTT_CLIENT_DISCONNECTED);
        clientPingCount = registry.counter(MQTT_CLIENT_PING);
        new Thread(new MetresOnlineTask()).start();
    }

    public class MetresOnlineTask implements Runnable {
        @Override
        public void run() {
            while (!stopCount){
                logger.info("更新在线设备统计数[{}]",sessionManager.getOnlineSize());
                clientOnlineCount.set(sessionManager.getOnlineSize());
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
