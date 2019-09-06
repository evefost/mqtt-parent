package com.xie.mqtt.controller;

import com.eve.broker.core.Session;
import com.eve.broker.core.SessionRegistry;
import com.eve.broker.core.subscriptions.Subscription;
import com.xie.mqtt.common.EventCodeEnum;
import com.xie.mqtt.common.proto.MqttMessagePb;
import com.xie.mqtt.common.proto.ServerNotifyPb;
import com.xie.mqtt.mq.SessionManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

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



    /**
     * 模拟消息发送
     * @return
     */
    @GetMapping("/send/notify")
    boolean notifyClient() {
        ConcurrentMap<String, Session> sessions = sessionManager.getSessions();
        sessions.forEach((clientId,session)->{
            String topic="/topic/"+clientId;
            MqttMessagePb.MqttMessage.Builder builder = buildBoxMessage(clientId);
            MqttMessagePb.MqttMessage message = builder.build();
            byte[] payload = message.toByteArray();
            MqttPublishMessage publish = MqttMessageBuilders.publish()
                    .messageId(1)
                    .topicName(topic)
                    .retained(false)
                    .qos(MqttQoS.AT_MOST_ONCE)
                    .payload(Unpooled.copiedBuffer(payload)).build();
            sessionManager.publish(publish);

        });

        return true;
    }



    private MqttMessagePb.MqttMessage.Builder buildBoxMessage(String clientId) {
        MqttMessagePb.MqttMessage.Builder messageBuilder = MqttMessagePb.MqttMessage.newBuilder();
        MqttMessagePb.MqttHead.Builder headBuilder = MqttMessagePb.MqttHead.newBuilder();
        headBuilder.setDeviceId(clientId);
        headBuilder.setMessageId(UUID.randomUUID().toString());
        headBuilder.setCc(1);
        headBuilder.setEventCode(EventCodeEnum.SERVER_NOTIFY.getCode());
        messageBuilder.setHead(headBuilder);

        ServerNotifyPb.ServerNotify.Builder notifyBuilder = ServerNotifyPb.ServerNotify.newBuilder();

        ServerNotifyPb.ServerNotify notify = notifyBuilder.build();
        messageBuilder.setBody(notify.toByteString());
        return messageBuilder;
    }



}
