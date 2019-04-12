package com.xhg.mqtt.mq.handler.up;


import static com.xhg.mqtt.common.EventCodeEnum.DEVICE_WILL;

import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.Message;

/**
 * 设备异常，遗嘱处理
 */
public class DeviceWillHandler  extends AbstractUpHandler {

    public DeviceWillHandler(MessageClient client) {
        super(client);
    }

    @Override
    protected void doAck(Message message) {

    }

    @Override
    public String getEventCode() {
        return DEVICE_WILL.getCode();
    }


    @Override
    protected <TM extends Message> void doProcess(TM message) {
        logger.warn("监听到设备掉线异常 deviceId[{}]", message.getBuzMessage().getHead().getDeviceId());
        message.setTopic("xhg-order-device");
        String deviceId = message.getBuzMessage().getHead().getDeviceId();


    }




}
