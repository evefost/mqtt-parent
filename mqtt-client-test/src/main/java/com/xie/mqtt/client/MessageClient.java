package com.xie.mqtt.client;

/**
 * Created by xieyang on 19/3/31.
 */
public interface MessageClient<M> {

    String getClientId();

    void send(M msg);

    void onReceived(String topic,M msg);

    void onClosed(Throwable cause);


    ClientOptions getOptions();

    void disconnect();

    void reconnect(boolean immediately);

    void connect();

    void subscript();
}
