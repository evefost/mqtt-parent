package com.eve.mqtt.client;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 类说明
 * <p>
 *
 * @author xieyang
 * @version 1.0.0
 * @date 2019/9/6
 */
public interface Connection {

    AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");

    void setChannel(Channel channel);

    Channel getChannel();

    void close();

    String getPoolKey();

    void setPoolKey(String poolKey);

    String getChannelId();

    void setChannelId(String channelId);
}
