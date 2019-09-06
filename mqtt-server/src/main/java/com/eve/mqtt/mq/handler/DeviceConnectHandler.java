package com.eve.mqtt.mq.handler;


import com.eve.mqtt.common.handler.AbstractHandler;
import io.netty.handler.codec.mqtt.MqttConnectMessage;

/**
 * 终端登录处理
 *
 * @author xie
 */
public class DeviceConnectHandler extends AbstractHandler<MqttConnectMessage> {

    @Override
    public boolean support(Object message) {
        if (message instanceof MqttConnectMessage) {
            return true;
        }
        return false;
    }

    @Override
    protected <IM> void doProcess(IM message) {
        MqttConnectMessage mqMessage = (MqttConnectMessage) message;
        String clientId = mqMessage.payload().clientIdentifier();
        logger.info("监听到设备连接clientId[{}]", clientId);

    }

    @Override
    public <O> O decodeContent(MqttConnectMessage message, Class<O> outputClass) {
        return null;
    }


}
