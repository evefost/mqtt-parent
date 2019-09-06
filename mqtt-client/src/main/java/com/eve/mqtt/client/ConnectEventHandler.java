package com.eve.mqtt.client;


import com.eve.mqtt.client.command.SendConnectHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class ConnectEventHandler extends ChannelDuplexHandler {

    private ConnectManager connectManager;

    public ConnectManager getConnectManager() {
        return connectManager;
    }

    public void setConnectManager(ConnectManager connectManager) {
        this.connectManager = connectManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SendConnectHandler handler = new SendConnectHandler();
        RemotingContext context = new RemotingContext(ctx, false);
        handler.handleCommand(context, null);
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
