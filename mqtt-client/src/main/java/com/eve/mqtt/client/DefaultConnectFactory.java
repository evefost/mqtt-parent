package com.eve.mqtt.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类说明
 * <p>
 *
 * @author 谢洋
 * @version 1.0.0
 * @date 2019/9/6
 */
public class DefaultConnectFactory implements ConnectFactory {


    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectFactory.class);

    private volatile boolean isInit;

    private ChannelHandler handler;

    private ChannelHandler heartbeatHandler;

    private Bootstrap bootstrap;


    @Override
    public void init() {
        if (isInit) {
            return;
        }
        isInit = true;
        EventLoopGroup workerGroup = new NioEventLoopGroup(20);
        try {
            handler = new ClientMqttHandler();
            heartbeatHandler = new HeartbeatHandler();
            ConnectEventHandler connectEventHandler = new ConnectEventHandler();
            connectEventHandler.setConnectManager(null);
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("decoder", new MqttDecoder());
                    pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    pipeline.addLast("heartbeatHandler", heartbeatHandler);
                    pipeline.addLast("handler", handler);
                }
            });
        } catch (Throwable throwable) {
            logger.error("Error received in client setup", throwable);
            workerGroup.shutdownGracefully();
        }
    }


    @Override
    public Connection createConnection(Url url) throws Exception {
        return createConnection(url, null);
    }

    @Override
    public Connection createConnection(Url url, Class<? extends Connection> connectClass) throws Exception {
        Channel channel = bootstrap.connect(url.getHostIp(), url.getPort()).sync().channel();
        Connection connection;
        if (connectClass == null) {
            connection = new MqttConnection();
        } else {
            connection = connectClass.newInstance();
        }
        channel.attr(Connection.CONNECTION).set(connection);
        return connection;
    }


}

