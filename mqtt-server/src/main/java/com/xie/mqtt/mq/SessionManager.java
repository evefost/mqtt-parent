package com.xie.mqtt.mq;

import com.eve.broker.core.Session;
import com.eve.broker.core.SessionRegistry;
import com.eve.broker.core.subscriptions.ISubscriptionsDirectory;
import com.eve.broker.core.subscriptions.Subscription;
import com.eve.broker.core.subscriptions.Topic;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static io.netty.handler.codec.mqtt.MqttQoS.*;


public class SessionManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SessionRegistry sessionRegistry;

    private ISubscriptionsDirectory subscriptions;

    public SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }


    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public int getOnlineSize() {
        return sessionRegistry.getSessions().size();
    }


    public ConcurrentMap<String, Session> getSessions() {
        return sessionRegistry.getSessions();

    }

    public Session getSession(Object clientId) {
        return sessionRegistry.getSessions().get(clientId);
    }

    public void setSubscriptions(ISubscriptionsDirectory subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void publish(MqttPublishMessage msg) {

        final MqttQoS qos = msg.fixedHeader().qosLevel();
        final String topicName = msg.variableHeader().topicName();
        final String clientId = "system";
        logger.trace("Processing PUBLISH message. CId={}, topic: {}, messageId: {}, qos: {}", clientId, topicName,
                msg.variableHeader().packetId(), qos);
        ByteBuf payload = msg.payload();
        final boolean retain = msg.fixedHeader().isRetain();
        final Topic topic = new Topic(topicName);
        if (!topic.isValid()) {
            logger.debug("Drop connection because of invalid topic format");

        }
        switch (qos) {
            case AT_MOST_ONCE:
                publish2Subscribers(payload, topic, AT_MOST_ONCE);
                break;
            case AT_LEAST_ONCE: {
                publish2Subscribers(payload, topic, AT_LEAST_ONCE);
                //todo
                break;
            }
            case EXACTLY_ONCE: {
                publish2Subscribers(payload, topic, EXACTLY_ONCE);
                break;
            }
            default:
                logger.error("Unknown QoS-Type:{}", qos);
                break;
        }
    }


    private void publish2Subscribers(ByteBuf origPayload, Topic topic, MqttQoS publishingQos) {
        Set<Subscription> topicMatchingSubscriptions = subscriptions.matchQosSharpening(topic);

        for (final Subscription sub : topicMatchingSubscriptions) {
            MqttQoS qos = lowerQosToTheSubscriptionDesired(sub, publishingQos);
            Session targetSession = getSession(sub.getClientId());

            boolean isSessionPresent = targetSession != null;
            if (isSessionPresent) {
                logger.debug("Sending PUBLISH message to active subscriber CId: {}, topicFilter: {}, qos: {}",
                        sub.getClientId(), sub.getTopicFilter(), qos);
                // we need to retain because duplicate only copy r/w indexes and don't retain() causing refCnt = 0
                ByteBuf payload = origPayload.retainedDuplicate();
                targetSession.sendPublishOnSessionAtQos(topic, qos, payload);
            } else {
                // If we are, the subscriber disconnected after the subscriptions tree selected that session as a
                // destination.
                logger.debug("PUBLISH to not yet present session. CId: {}, topicFilter: {}, qos: {}", sub.getClientId(),
                        sub.getTopicFilter(), qos);
            }
        }
    }

    static MqttQoS lowerQosToTheSubscriptionDesired(Subscription sub, MqttQoS qos) {
        if (qos.value() > sub.getRequestedQos().value()) {
            qos = sub.getRequestedQos();
        }
        return qos;
    }
}
