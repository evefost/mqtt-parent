package com.eve.mqtt.client;

/**
 * 类说明
 * <p>
 *
 * @author 谢洋
 * @version 1.0.0
 * @date 2019/9/6
 */
public class MqttClient implements RemotingClient{

    private ConnectManager connectManager;

    public void setConnectManager(ConnectManager connectManager){
        this.connectManager = connectManager;
    }


    @Override
    public void start() {
        connectManager.start();
    }

    @Override
    public void shutdown() {

    }
}
