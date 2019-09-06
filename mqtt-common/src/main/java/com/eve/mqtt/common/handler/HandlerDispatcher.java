package com.eve.mqtt.common.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理器
 *
 * @author xie
 */
public class HandlerDispatcher {

    protected static final Logger logger = LoggerFactory.getLogger(HandlerDispatcher.class);

    private final static List<Handler> handlers = new ArrayList<>();

    public final static  boolean process(Object message) {
        try {
            for (Handler handler : handlers) {
                if (handler.support(message)) {
                    handler.processMessage(message);
                    return true;
                }
            }
        } catch (Throwable throwable) {
            logger.error("消息处理失败:", throwable);
        }
        return false;
    }

    public static void addHandler(Handler handler) {
        handlers.add(handler);
    }

    public static void addAllHandler(List<Handler> handlerList) {
        handlers.addAll(handlerList);
    }


}
