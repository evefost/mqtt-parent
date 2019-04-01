package io.moquette.broker.listener;

/**
 * @author xie
 */
public interface MqttListener<M> {

    /**
     * 消息输入
     */
    void input(M message);

    void output(M message);

    void close();
}
