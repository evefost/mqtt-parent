package com.xhg.mqtt.mq.handler.up;


import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.Message;

public class DeviceDefaultHandler extends AbstractUpHandler{

    public DeviceDefaultHandler(MessageClient client) {
        super(client);
    }

    @Override
    public String getEventCode() {
        return "default";
    }


    @Override
    protected <TM extends Message> void doProcess(TM message) {
        logger.debug("设备消息默认处理");
        message.setTopic("xhg-order-device");

    }


}
