package com.eve.mqtt.client2;

import io.netty.channel.Channel;

import java.util.List;

/**
 * 类说明
 * <p>
 *
 * @author 谢洋
 * @version 1.0.0
 * @date 2019/9/6
 */
public class MqttConnection implements Connection {

    private Channel channel;

    private String poolKey;

    private String channelId;

    private List<String> topics;

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }


    @Override
    public void close() {
        channel.close();
    }

    @Override
    public String getPoolKey() {
        return this.poolKey;
    }

    @Override
    public void setPoolKey(String poolKey) {
        this.poolKey = poolKey;
    }

    @Override
    public String getChannelId() {
        return channelId;
    }

    @Override
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
