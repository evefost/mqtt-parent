package com.xhg.mqtt.mq.metrics;


import com.xhg.mqtt.mq.MessageInputListener;
import com.xhg.mqtt.mq.MessageOutputListener;
import com.xhg.mqtt.mq.POINT;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.proto.MqttMessagePb.MqttMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息到达率统计
 */
@Component
public class ArrivalRateStatisticsListener implements MessageInputListener,MessageOutputListener,InitializingBean {

    @Autowired
    private MeterRegistry registry;

    private Counter ackArrivalCount;

    private Counter needAckCount;


    @Override
    public void input(Message message) {
        if(message.getFrom().equals(POINT.MQTT)){
            if(message.getTopic().contains("/ack")){
                ackArrivalCount.increment();
            }
        }
    }

    @Override
    public void output(Message message) {
        if(message.getTo().equals(POINT.MQTT)){
            MqttMessage mqttMessage = message.getMqttMessage();
            if(mqttMessage.getHead().getCc()==1){
                needAckCount.increment();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ackArrivalCount = registry.counter("mqtt_output_ack");
        needAckCount = registry.counter("mqtt_output_ack_need");
    }
}
