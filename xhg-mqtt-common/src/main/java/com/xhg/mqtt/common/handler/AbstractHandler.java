package com.xhg.mqtt.common.handler;

import com.xhg.mqtt.common.POINT;
import com.xhg.mqtt.common.ProcessHook;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象模板勾子处理
 *
 * @author xie
 */
public abstract class AbstractHandler<M> implements Handler<MqttMessage> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);

    private static List<ProcessHook> hooks = new ArrayList<>();


    @Override
    public void processMessage(MqttMessage message) {
        doBefore(message);
        try {
            doProcess(message);
        } finally {
            doAfter(message);
        }
    }


    /**
     * 处理相应逻辑，执行该方法时消息已被解码处理
     * @param message
     * @param <IM>
     */
    protected abstract <IM> void doProcess(IM message);


    public static void registHook(ProcessHook hook) {
        hooks.add(hook);
    }

    public static void unRegisterHook(ProcessHook hook) {
        hooks.remove(hook);
    }


    private void doAfter(MqttMessage message) {
        for (ProcessHook hook : hooks) {
            hook.afterProcess(message);
        }
    }


    protected void doBefore(MqttMessage message) {

        for (ProcessHook hook : hooks) {
            hook.beforeProcess(message);
        }
    }

    @Override
    public POINT getPoint() {
        return POINT.CLIENT;
    }

    public abstract  <O> O decodeContent(M message,Class<O> outputClass);


}
