package com.xhg.mqtt.controller;


import static java.nio.charset.StandardCharsets.UTF_8;

import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.common.bo.ChangeClientNumber;
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
 * 测试用
 * @author xie
 */
@RestController
@RequestMapping("/test")
public class TestController {


    @Autowired
    private SessionManager sessionManager;

    private AtomicInteger messageId = new AtomicInteger(0);


    /**
     * 关闭指定客户端
     * @param clientId
     * @return
     */
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
     * 通知客户端，重置所有连接
     * @return
     */
    @GetMapping("/reset/clients")
    boolean resetClients(int count) {
        ChangeClientNumber change = new ChangeClientNumber();
        change.setCount(count);
        change.setDescription("客户端重置所有连接");
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(SystemCmd.TEST_RESET_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(change.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;
    }


    /**
     * 通知客户端，主动关闭部分或全部客户端
     * @return
     */
    @GetMapping("/disconnect")
    boolean disconnect(int count) {
        ChangeClientNumber change = new ChangeClientNumber();
        change.setCount(count);
        change.setDescription("主动关闭部分或全部客户端");
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(SystemCmd.TEST_DISCONNECT_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(change.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;

    }

    /**
     * 通知客户端，主动增加连接数
     * @return
     */
    @GetMapping("/increase/clients")
    boolean increaseClients(int count) {
        ChangeClientNumber change = new ChangeClientNumber();
        change.setCount(count);
        change.setDescription("通知客户端，主动增加连接数");
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .messageId(messageId.incrementAndGet())
            .topicName(SystemCmd.TEST_INCREASE_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(change.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;
    }



}
