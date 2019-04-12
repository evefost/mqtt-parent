package com.xhg.mqtt.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xieyang on 19/4/1.
 */
public class NettyClientStarter {

    private static  Logger logger = LoggerFactory.getLogger(NettyClientStarter.class);

    private static volatile NettyClientStarter instance;


    private final ClientNettyMQTTHandler handler = new ClientNettyMQTTHandler();

    private EventLoopGroup workerGroup;

    private Bootstrap bootstrap;

    private NettyClientStarter(){
        init();
    }

    public static NettyClientStarter getInstance(){
        if(instance == null){
           synchronized (NettyClientStarter.class){
               if(instance == null){
                   instance = new NettyClientStarter();
               }
           }
        }
        return instance;
    }


    private void init(){

        workerGroup = new NioEventLoopGroup(20);
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("decoder", new MqttDecoder());
                    pipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    pipeline.addLast("handler", handler);
                }
            });
        } catch (Exception ex) {
            logger.error("Error received in client setup", ex);
            workerGroup.shutdownGracefully();
        }
    }

    public Bootstrap getBootstrap(){
        return bootstrap;
    }

}
