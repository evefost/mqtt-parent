package com.xhg.mqtt.controller;

import com.xhg.mqtt.mq.SessionManager;
import io.moquette.broker.Session;
import io.moquette.broker.SessionRegistry;
import io.moquette.broker.subscriptions.Subscription;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}