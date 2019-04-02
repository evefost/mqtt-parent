package com.xhg.mqtt.mq.handler.up;


import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * 终端登录处理
 *
 * @author xie
 */
public class DeviceConnectHandler extends AbstractUpHandler {

    public DeviceConnectHandler(MessageClient client) {
        super(client);
    }

    @Override
    public String getEventCode() {
        return "";
    }


    @Override
    protected void doProcess(MqttWrapperMessage message) {

        MqttMessage mqMessage = message.getSrcMessage();
        String clientId = ((MqttConnectPayload) mqMessage.payload()).clientIdentifier();
        if (logger.isDebugEnabled()) {
            logger.debug("监听到设备连接deviceId[{}]", clientId);
        }
    }

    @Override
    protected boolean isAck(Message message) {
        return false;
    }

    @Override
    protected boolean isNeedAck(Message message) {
        return false;
    }


}
