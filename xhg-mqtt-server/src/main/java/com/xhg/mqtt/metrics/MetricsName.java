package com.xhg.mqtt.metrics;

public interface MetricsName {

    /**
     * mqtt 入站信息量
     */
    String MQTT_MESSAGE_INPUT ="mqtt_message_input";
    /**
     * mqtt 出站信息量
     */
    String MQTT_MESSAGE_OUTPUT ="mqtt_message_output";

    /**
     * mqtt在线设备数
     */
    String MQTT_DEVICE_ONLINE ="mqtt_client_online";


    /**
     * mqtt 客户端上线统计
     */
    String MQTT_CLIENT_CONNECT ="mqtt_client_connect";

    /**
     * mqtt 客户端掉线统计
     */
    String MQTT_CLIENT_DISCONNECTED ="mqtt_client_disconnected";

    /**
     * mqtt 客户端心跳速率
     */
    String MQTT_CLIENT_PING ="mqtt_client_ping";


}
