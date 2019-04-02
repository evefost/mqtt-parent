package com.xhg.mqtt.mq.handler.up;


import static com.xhg.mqtt.common.EventCodeEnum.SITE_INFO;

import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;

public class SiteInfoHandler extends AbstractUpHandler{

    public SiteInfoHandler(MessageClient client) {
        super(client);
    }

    @Override
    public String getEventCode() {
        return SITE_INFO.getCode();
    }


    @Override
    protected void doProcess(MqttWrapperMessage message) {
        logger.debug("监听到设备上报站点信息");
        message.setTopic("xhg-order-device");
    }

}
