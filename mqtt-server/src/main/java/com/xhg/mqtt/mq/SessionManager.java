package com.xhg.mqtt.mq;

import io.moquette.broker.Session;
import io.moquette.broker.SessionRegistry;



public class SessionManager {

    private SessionRegistry sessionRegistry;

    public SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }


    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public int getOnlineSize() {
        return sessionRegistry.getSessions().size();
    }

    public Session getSession(Object clientId) {
        return sessionRegistry.getSessions().get(clientId);
    }


}
