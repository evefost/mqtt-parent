package com.xhg.mqtt.handler.down;


import static com.xhg.mqtt.common.EventCodeEnum.SERVER_NOTIFY;

import com.xhg.mqtt.handler.AbstractCustomerHandler;
import org.springframework.stereotype.Component;

/**
 * @author xieyang
 */
@Component
public class ServiceNotifyHandler extends AbstractCustomerHandler {


    @Override
    protected String getEventCode() {
        return SERVER_NOTIFY.getCode();
    }

    @Override
    protected <IM> void doProcess(IM message) {
        logger.info("client 收到notify 消息");
    }
}
