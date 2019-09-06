package com.eve.mqtt.client.command;


import com.eve.mqtt.client.Connection;
import com.eve.mqtt.client.MqttConnection;
import com.eve.mqtt.client.RemotingContext;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;


/**
 * @author Administrator
 */
public class SendSubscriptHandler implements CommandHandler {

    protected static final Logger logger = LoggerFactory.getLogger(SendSubscriptHandler.class);

    private static AtomicInteger id = new AtomicInteger(0);

    @Override
    public int commandCode() {
        return MqttMessageType.CONNACK.value();
    }

    @Override
    public void handleCommand(RemotingContext ctx, Object msg) throws Exception {
        Channel channel = ctx.getChannelContext().channel();
        MqttConnection connection = (MqttConnection) channel.attr(Connection.CONNECTION).get();
        logger.info("[{}] 订阅主题:{}", connection.getChannelId(), connection.getTopics());
        MqttMessageBuilders.SubscribeBuilder subBuilder = MqttMessageBuilders.subscribe();
        for (String t : connection.getTopics()) {
            subBuilder.addSubscription(AT_MOST_ONCE, t);
        }
        MqttSubscribeMessage message = subBuilder.messageId(id.incrementAndGet()).build();
        channel.writeAndFlush(message);
    }
}
