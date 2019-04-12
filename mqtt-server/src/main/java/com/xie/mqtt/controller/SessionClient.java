package com.xie.mqtt.controller;

import java.util.List;

/**
 * Created by xieyang on 19/3/31.
 */
public class SessionClient {

   private String clientId;

   private List<String> topics;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
