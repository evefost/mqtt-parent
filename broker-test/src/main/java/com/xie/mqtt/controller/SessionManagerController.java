package com.xie.mqtt.controller;

import com.xie.mqtt.MqttBrokerApplication;
import io.moquette.broker.Server;
import io.moquette.broker.Session;
import io.moquette.broker.SessionRegistry;
import io.moquette.broker.subscriptions.Subscription;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xieyang on 19/3/30.
 */
@RestController
@RequestMapping("admin")
public class SessionManagerController {


    private static final Logger logger = LoggerFactory.getLogger(SessionManagerController.class);


    List<MqttClient> clients = new ArrayList<MqttClient>();

    private AtomicInteger clientCount = new AtomicInteger(0);

    @GetMapping("/sessions/ByPage")
    Page<SessionClient> getSessionsByPage(int currentPage, int pageSize) throws MqttException {
        Server mqttBroker = MqttBrokerApplication.mqttBroker;

        SessionRegistry sessionRegistry = mqttBroker.getSessionRegistry();
        ConcurrentMap<String, Session> sessions = sessionRegistry.getSessions();
        List<Session> sessionList = new ArrayList<>(sessions.values());


        Page<SessionClient>   page = new Page<>(new ArrayList<>(0),pageSize);

        if (sessionList.isEmpty()) {

            return page;
        }
        Page<Session> tempage = new Page<>(sessionList, pageSize);
        tempage.setCurrentPage(currentPage);
        List<Session> items = tempage.getItems();
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
        page.setTotal(tempage.getTotal());
        page.setTotalPages(tempage.getTotalPages());
        page.updateCurrentPage(currentPage);
        return page;

    }

}
