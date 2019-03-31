package com.xie.mqtt;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;

import static java.nio.charset.StandardCharsets.UTF_8;

public  class PublisherListener extends AbstractInterceptHandler {

        @Override
        public String getID() {
            return "MqttBrokerApplicationPublishListener";
        }

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            ByteBuf payload = msg.getPayload();
            byte[] content = new byte[payload.readableBytes()];
            payload.readBytes(content);
            final String decodedPayload = new String(content, UTF_8);

            System.out.println("收到消息发布 topic: " + msg.getTopicName() + " content: " + decodedPayload);
        }
    }