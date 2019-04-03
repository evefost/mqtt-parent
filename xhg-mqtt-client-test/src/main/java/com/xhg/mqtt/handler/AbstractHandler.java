package com.xhg.mqtt.handler;

import com.xhg.mqtt.common.POINT;
import com.xhg.mqtt.common.ProcessHook;
import com.xhg.mqtt.common.handler.Handler;
import io.netty.handler.codec.mqtt.MqttMessage;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     */
    protected abstract <T extends MqttMessage> void doProcess(T message);


    public static void registHook(ProcessHook hook) {
        hooks.add(hook);
    }

    public static void unRegister(ProcessHook hook) {
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
