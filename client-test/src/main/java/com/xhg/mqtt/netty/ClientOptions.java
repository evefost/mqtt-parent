package com.xhg.mqtt.netty;

/**
 *
 * @author xieyang
 * @date 19/3/31
 */
public class ClientOptions implements Cloneable{

    private String[] brokerNodes;

    private String[] topics;

    private int keepAlive=120;

    private boolean autoReconnect;

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

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
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
    protected ClientOptions clone() throws CloneNotSupportedException {
        ClientOptions clone = (ClientOptions) super.clone();
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
