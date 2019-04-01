package com.xhg.mqtt.mq.handler.up;


import static com.xhg.mqtt.mq.EventCodeEnum.DEVICE_WILL;

import com.xhg.mqtt.mq.SessionManager;
import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;

/**
 * 设备异常，遗嘱处理
 */
public class DeviceWillHandler  extends AbstractUpHandler {

    public DeviceWillHandler(MessageClient client) {
        super(client);
    }

    @Override
    protected void doAck(MqttWrapperMessage message) {

    }

    @Override
    public String getEventCode() {
        return DEVICE_WILL.getCode();
    }


    @Override
    protected void doProcess(MqttWrapperMessage message) {
        logger.warn("监听到设备掉线异常 deviceId[{}]",message.getMqttMessage().getHead().getDeviceId());
        message.setTopic("xhg-order-device");
        String deviceId = message.getMqttMessage().getHead().getDeviceId();
        SessionManager.remove(deviceId);

    }




}
