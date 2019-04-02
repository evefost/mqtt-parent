package com.xhg.mqtt.handler;


import io.netty.handler.codec.mqtt.MqttMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理器
 *
 * @author xie
 */
public class HandlerDispatcher {


    private final static List<Handler<MqttMessage>> handlers = new ArrayList<>();

    public final static <M extends MqttMessage> boolean process(M message) {
        for (Handler handler : handlers) {
            if (handler.support(message)) {
                handler.processMessage(message);
                return true;
            }
        }
        return false;
    }

    public static void addHandler(Handler handler) {
        handlers.add(handler);
    }


}
