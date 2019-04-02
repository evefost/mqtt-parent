package com.xhg.mqtt.mq.handler.up;


import static com.xhg.mqtt.common.EventCodeEnum.SERVICE_DEVICE_ADD;

import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;

public class DeviceAddHandler extends AbstractUpHandler {


    public DeviceAddHandler(MessageClient client) {
        super(client);
    }


    @Override
    public String getEventCode() {
        return SERVICE_DEVICE_ADD.getCode();
    }



    @Override
    protected void doProcess(MqttWrapperMessage message) {
        logger.debug("监听到设备添加");
        message.setTopic("xhg-order-device");
        client.publish(message);
    }



}
