package com.xie.mqtt.metrics;

public interface MetricsName {

    /**
     * mqtt 入站信息量
     */
    String MQTT_MESSAGE_INPUT ="mqtt_input_message";
    /**
     * mqtt 出站信息量
     */
    String MQTT_MESSAGE_OUTPUT ="mqtt_output_message";

    /**
     * mqtt 出站信息量
     */
    String MQTT_MESSAGE_TOTAL ="mqtt_message";

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


    /**
     * mqtt 出站字节量
     */
    String MQTT_OUTPUT_BYTES ="mqtt_output_bytes";


    /**
     * mqtt 入站字节量
     */
    String MQTT_INPUT_BYTES ="mqtt_input_bytes";


}
