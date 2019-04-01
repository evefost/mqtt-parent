package com.xhg.mqtt.mq.handler.up;


import static com.xhg.mqtt.mq.EventCodeEnum.DEVICE_LOGIN;

import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;

/**
 * 终端登录处理
 * @author xie
 */
public class DeviceLoginHandler  extends AbstractUpHandler {

    public DeviceLoginHandler(MessageClient client) {
        super(client);
    }

    @Override
    public String getEventCode() {
        return DEVICE_LOGIN.getCode();
    }


    @Override
    protected void doProcess(MqttWrapperMessage message) {
        if(logger.isDebugEnabled()){
            logger.debug("监听到设备上线deviceId[{}]", message.getBuzMessage().getHead().getDeviceId());
        }
        String deviceId = message.getBuzMessage().getHead().getDeviceId();
//        message.setTopic("xhg-order-device");
//        client.publish(message);
    }


}
