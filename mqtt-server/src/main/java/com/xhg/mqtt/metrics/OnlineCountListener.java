package com.xhg.mqtt.metrics;


import com.xhg.mqtt.mq.MessageInputListener;
import com.xhg.mqtt.mq.POINT;
import com.xhg.mqtt.mq.SessionManager;
import com.xhg.mqtt.mq.message.Message;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计设备在线数
 */
@Component
public class OnlineCountListener implements MessageInputListener, InitializingBean {

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private SessionManager sessionManager;


    private AtomicInteger deviceOnlineCount;


    @Override
    public void input(Message message) {
        if (message.getFrom().equals(POINT.MQTT)) {
            MqttMessage mqttMessage = (MqttMessage) message.getSrcMessage();
            if (MqttMessageType.CONNECT == mqttMessage.fixedHeader().messageType()) {
                deviceOnlineCount.set(sessionManager.getOnlineSize());
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        deviceOnlineCount = registry.gauge("mqtt_device_online", new AtomicInteger(0));
    }




}
