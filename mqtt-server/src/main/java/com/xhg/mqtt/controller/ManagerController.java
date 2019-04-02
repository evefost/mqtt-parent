package com.xhg.mqtt.controller;

import com.xhg.mqtt.mq.SessionManager;
import io.moquette.broker.Session;
import io.moquette.broker.SessionRegistry;
import io.moquette.broker.subscriptions.Subscription;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by xieyang on 19/3/30.
 */
@RestController
@RequestMapping("admin")
public class ManagerController {


    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);


    @Autowired
    private SessionManager sessionManager;

    @GetMapping("/sessions/ByPage")
    Page<SessionClient> getSessionsByPage(int currentPage, int pageSize)  {

        SessionRegistry sessionRegistry = sessionManager.getSessionRegistry();
        ConcurrentMap<String, Session> sessions = sessionRegistry.getSessions();
        List<Session> sessionList = new ArrayList<>(sessions.values());
        Page<SessionClient>   page = new Page<>(new ArrayList<>(0),pageSize);
        if (sessionList.isEmpty()) {
            return page;
        }
        Page<Session> temPage = new Page<>(sessionList, pageSize);
        temPage.setCurrentPage(currentPage);
        List<Session> items = temPage.getItems();
        List<SessionClient> clients = new ArrayList<>(items.size());
        for(Session s:items){
            SessionClient c = new SessionClient();
            c.setClientId(s.getClientID());

            List<Subscription> subscriptions = s.getSubscriptions();
            List<String> topics = new ArrayList<>(subscriptions.size());
            for (Subscription sub:subscriptions) {
                topics.add(sub.getTopicFilter().toString());
            }
            c.setTopics(topics);
            clients.add(c);
        }
        page.setItems(clients);
        page.setTotal(temPage.getTotal());
        page.setTotalPages(temPage.getTotalPages());
        page.updateCurrentPage(currentPage);
        return page;

    }

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

    @GetMapping("/disconnect/all")
    boolean closeAll() {
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName("/topic/disconnect")
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer("关闭所有客户端".getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;

    }

    @GetMapping("/reset/clients")
    boolean resetClients() {
        MqttPublishMessage publish = MqttMessageBuilders.publish()
                .topicName("/topic/reset")
                .retained(false)
                .qos(MqttQoS.AT_MOST_ONCE)
                .payload(Unpooled.copiedBuffer("重置所有客户端".getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;

    }

}
