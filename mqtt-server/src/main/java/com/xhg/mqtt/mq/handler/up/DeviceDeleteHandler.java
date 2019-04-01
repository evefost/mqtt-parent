package com.xhg.mqtt.mq.handler.up;


import static com.xhg.mqtt.mq.EventCodeEnum.SERVICE_DEVICE_REMOVE;

import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;

public class DeviceDeleteHandler extends AbstractUpHandler {


    public DeviceDeleteHandler(MessageClient client) {
        super(client);
    }


    @Override
    public String getEventCode() {
        return SERVICE_DEVICE_REMOVE
            .getCode();
    }


    @Override
    protected void doProcess(MqttWrapperMessage message) {
        logger.debug("监听到设备删除");
        message.setTopic("xhg-order-device");
        client.publish(message);
    }



}
