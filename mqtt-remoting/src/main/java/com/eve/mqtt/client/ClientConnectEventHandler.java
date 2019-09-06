package com.eve.mqtt.client;


import com.eve.mqtt.client.command.CommandHandler;
import com.eve.mqtt.client.command.CommandManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.*;

public class ClientConnectEventHandler extends ChannelDuplexHandler {

    private ConnectManager connectManager;

    public ConnectManager getConnectManager() {
        return connectManager;
    }

    public void setConnectManager(ConnectManager connectManager) {
        this.connectManager = connectManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Connection connection = channel.attr(Connection.CONNECTION).get();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(MqttVersion.MQTT_3_1.protocolName(), MqttVersion.MQTT_3_1.protocolLevel(), false, false, false, 1, false,
                true, 60);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(connection.getChannelId(), null, null, null, (byte[]) null);
        MqttConnectMessage message = new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
        CommandHandler handler = CommandManager.getCommandHandler(message);
        RemotingContext context = new RemotingContext(ctx, false);
        handler.handleCommand(context, message);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection conn = ctx.channel().attr(Connection.CONNECTION).get();
        if (conn != null) {
            connectManager.remove(conn);
        }
        super.channelInactive(ctx);
    }
}
