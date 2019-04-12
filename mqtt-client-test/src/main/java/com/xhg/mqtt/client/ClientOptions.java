package com.xhg.mqtt.client;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author xieyang
 * @date 19/3/31
 */
@Component
public class ClientOptions implements Cloneable{

    private String[] brokerNodes;

    private List<String> topics;

    private int keepAlive=120;

    private boolean autoReconnect=true;

    private Node selectNode;

    public Node getSelectNode() {
        return selectNode;
    }

    public void setSelectNode(Node selectNode) {
        this.selectNode = selectNode;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String[] getBrokerNodes() {
        return brokerNodes;
    }

    public void setBrokerNodes(String[] brokerNodes) {
        this.brokerNodes = brokerNodes;
    }

    @Override
    public ClientOptions clone() {
        ClientOptions clone = null;
        try {
            clone = (ClientOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<String> cloneTopics = new ArrayList<>(10);
        for(String topic:topics){
            cloneTopics.add(topic);
        }
        clone.setTopics(cloneTopics);
        return clone;
    }

    public static class Node{

        private String host;

        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
