package com.eve.mqtt.client.command;

import com.eve.mqtt.client.Connection;
import com.eve.mqtt.client.RemotingContext;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Administrator
 */
public class SendConnectHandler implements CommandHandler {

    protected static final Logger logger = LoggerFactory.getLogger(SendConnectHandler.class);

    @Override
    public int commandCode() {
        return MqttMessageType.CONNECT.value();
    }

    @Override
    public void handleCommand(RemotingContext ctx, Object msg) throws Exception {
        Channel channel = ctx.getChannelContext().channel();
        Connection connection = channel.attr(Connection.CONNECTION).get();
        logger.info("clientId[{}] 发送mqtt连接[{}]命令", connection.getChannelId(), connection.getPoolKey());
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(MqttVersion.MQTT_3_1.protocolName(), MqttVersion.MQTT_3_1.protocolLevel(), false, false, false, 1, false,
                true, 60);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(connection.getChannelId(), null, null, null, (byte[]) null);
        MqttConnectMessage message = new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
        channel.writeAndFlush(message);
    }
}
