package com.eve.mqtt.metrics;

import com.eve.broker.core.listener.MqttListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计消息设备信息
 */
@Component
public class BytesMetrics implements MqttListener<ByteBuf>, SmartInitializingSingleton {


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


    @Override
    public void input(ByteBuf message) {
        inputCounter.increment(message.readableBytes());
    }

    @Override
    public void output(ByteBuf message) {
        outputCounter.increment(message.readableBytes());
    }

    @Override
    public void close() {

    }

    @Override
    public void open() {

    }


    @Override
    public void afterSingletonsInstantiated() {
        inputCounter = registry.counter(MetricsName.MQTT_INPUT_BYTES);
        outputCounter = registry.counter(MetricsName.MQTT_OUTPUT_BYTES);
    }
}
