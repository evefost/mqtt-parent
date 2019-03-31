package com.xie.mqtt.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.apache.catalina.Executor;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.xie.mqtt.netty.ClientNettyMQTTHandler.ATTR_KEY_CLIENT_CHANNEL;

/**
 * created by xieyang on 19/3/31.
 * @author xieyang
 */
public class MqttNettyClient extends AbstractMessageClient {


    private Bootstrap bootstrap;

    public MqttNettyClient(ClientOptions options, String clientId, Channel channel,Bootstrap bootstrap) {
        super(options, clientId, channel);
        this.bootstrap = bootstrap;
    }


    @Override
    public void onReceived(MqttMessage msg) {

    }

    @Override
    public void onClosed(Throwable cause) {
        if(cause != null){
            logger.error("[{}]异常关闭",clientId,cause);
        }else {
            logger.warn("[{}]关闭",clientId);
        }
        if(options.isAutoReconnect()){

            if(!channel.isActive()){
                logger.info("[{}]重连",clientId);
                ClientOptions.Node node = options.getSelectNode();

                    service.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();
                            channel.attr(ATTR_KEY_CLIENT_CHANNEL).set(MqttNettyClient.this);
                            connect();
                            } catch (Exception e) {
                                logger.error("[{}]重连异常:",clientId,e);
                            }
                        }
                    });



            }
        }
    }

    private ExecutorService service = Executors.newFixedThreadPool(20);


}