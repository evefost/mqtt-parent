package com.xhg.mqtt.mq.handler.down;


import com.google.protobuf.AbstractMessage;
import com.xhg.mqtt.common.POINT;
import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.handler.AbstractBuzHandler;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.RocketWrapperMessage;

import static com.xhg.mqtt.common.EventCodeEnum.SERVER_NOTIFY;
import static com.xhg.mqtt.common.POINT.SERVER;

/**
 * @author xieyang
 */
public class ServiceNotifyHandler extends AbstractBuzHandler<RocketWrapperMessage> {


    public ServiceNotifyHandler(MessageClient client) {
        super(client);
    }

    @Override
    protected void doAck(Message message) {

    }

    @Override
    public String getEventCode() {
        return SERVER_NOTIFY.getCode();
    }

    @Override
    public POINT getPoint() {
        return SERVER;
    }


    @Override
    protected <TM extends Message> void doProcess(TM message) {
        logger.debug("处理下发广播信息");
        client.publish(message);
    }

    @Override
    protected <R extends AbstractMessage> R parseMessageBody(RocketWrapperMessage message) {
        return null;
    }


}
