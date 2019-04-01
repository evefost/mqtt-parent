package com.xhg.mqtt.mq.metrics;


import com.xhg.mqtt.mq.EventCodeEnum;
import com.xhg.mqtt.mq.MessageInputListener;
import com.xhg.mqtt.mq.POINT;
import com.xhg.mqtt.mq.SessionManager;
import com.xhg.mqtt.mq.client.ClientFactory;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttHead;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage;
import io.micrometer.core.instrument.MeterRegistry;
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

    private AtomicInteger clientCount;

    private AtomicInteger serverClientCount;

    @Override
    public void input(Message message) {
        if (message.getFrom().equals(POINT.MQTT)) {
            MqttMessage mqttMessage = message.getMqttMessage();
            MqttHead head = mqttMessage.getHead();
            if (EventCodeEnum.DEVICE_LOGIN.getCode().equals(head.getEventCode())) {
                if (!head.getDeviceId().startsWith("server")) {
                    clientCount.set(SessionManager.getonlineClients().size());
                }
            }
            if (EventCodeEnum.DEVICE_WILL.getCode().equals(head.getEventCode())) {
                if (!head.getDeviceId().startsWith("server")) {
                    clientCount.decrementAndGet();
                }
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        clientCount = registry.gauge("mqtt_device_online", new AtomicInteger(0));
        serverClientCount = registry.gauge("mqtt_server_online", new AtomicInteger(0));
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    serverClientCount.set(ClientFactory.getClientSize(true)+ClientFactory.getClientSize(false));
                }
            }
        }.start();
    }




}
