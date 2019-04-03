package com.xhg.mqtt.common.handler;


import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理器
 *
 * @author xie
 */
public class HandlerDispatcher {


    private final static List<Handler> handlers = new ArrayList<>();

    public final static  boolean process(Object message) {
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

    public static void addAllHandler(List<Handler> handlers) {
        handlers.addAll(handlers);
    }


}
