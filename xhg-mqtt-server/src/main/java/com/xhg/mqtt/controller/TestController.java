package com.xhg.mqtt.controller;


import static java.nio.charset.StandardCharsets.UTF_8;

import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.common.bo.IncreaseClient;
import com.xhg.mqtt.mq.PublisherListener;
import com.xhg.mqtt.mq.SessionManager;
import io.moquette.broker.Session;
import io.moquette.broker.SessionRegistry;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xie
 */
@RestController
@RequestMapping("/test")
public class TestController {


    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private PublisherListener publisherListener;

    @GetMapping("/close/client")
    boolean closeClient(String clientId) {
        SessionRegistry sessionRegistry = sessionManager.getSessionRegistry();
        ConcurrentMap<String, Session> sessions = sessionRegistry.getSessions();
        Session session = sessions.get(clientId);
        if(session == null){
            return false;
        }
        session.closeImmediately();
        return true;

    }

    /**
     * 通知客户端，主动关闭所有测试连接
     * @return
     */
    @GetMapping("/disconnect/all")
    boolean closeAll() {
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(SystemCmd.TEST_DISCONNECT_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer("关闭所有客户端".getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;

    }

    /**
     * 通知客户端，重置所有连接
     * @return
     */
    @GetMapping("/reset/clients")
    boolean resetClients() {
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(SystemCmd.TEST_RESET_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer("重置所有客户端".getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;
    }


    private AtomicInteger messageId = new AtomicInteger(0);
    /**
     * 通知客户端，重置所有连接
     * @return
     */
    @GetMapping("/increase/clients")
    boolean increaseClients(int count) {
        IncreaseClient increaseClient = new IncreaseClient();
        increaseClient.setCount(count);
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .messageId(messageId.incrementAndGet())
            .topicName(SystemCmd.TEST_INCREASE_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(increaseClient.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;
    }



}
