package com.xhg.mqtt.mq.handler;

import com.google.protobuf.AbstractMessage;
import com.xhg.mqtt.common.ProcessHook;
import com.xhg.mqtt.common.handler.Handler;
import com.xhg.mqtt.common.proto.MqttMessagePb.MqttMessage;
import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象模板勾子处理
 *
 * @author xie
 */
public abstract class AbstractHandler<M extends Message> implements Handler<Message> {

    protected static final String ACK_FLAG="/ack";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static List<ProcessHook> hooks = new ArrayList<>();

    protected MessageClient client;

    public AbstractHandler(MessageClient client){
        this.client = client;
    }

    @Override
    public boolean support(Object message) {

//        MqttHead head = message.getBuzMessage().getHead();
//        POINT from = message.getFrom();
//        if( getEventCode().equals(head.getEventCode())&& getPoint().equals(from)){
//            return true;
//        }
        return false;
    }


    @Override
    public void processMessage(Message message) {
        doBefore(message);
        try {
            boolean ack = isAck(message);
            if(ack){
                doProcessAck(message);
            }else {
                doProcess(message);
            }
            if(!ack && isNeedAck(message)){
                doAck(message);
            }
        } finally {
            doAfter(message);
        }
    }


    /**
     * 如果是需要ack的消息，该方法将被执行
     * @param message
     */
    protected void doAck(Message message) {
        if(logger.isDebugEnabled()){
            logger.debug(" ack 请求");
        }
    }



    /**
     * 处理相应逻辑，执行该方法时消息已被解码处理
     */
    protected abstract <TM extends Message>  void doProcess(TM message);


    /**
     *  处理ack消息，该方法被执行
     * @param message
     */
    protected  void doProcessAck(Message message){
        logger.debug("处理收到的ack消息");
        message.setTopic("xhg-order-ack");

    }


    public static void registHook(ProcessHook hook) {
        hooks.add(hook);
    }

    public static void unRegister(ProcessHook hook) {
        hooks.remove(hook);
    }


    private void doAfter(Message message) {
        for (ProcessHook hook : hooks) {
            hook.afterProcess(message);
        }
    }


    protected void doBefore(Message message) {

        for (ProcessHook hook : hooks) {
            hook.beforeProcess(message);
        }
    }


    protected   boolean isAck(Message message){
        String topic = message.getTopic();
        if(StringUtils.isEmpty(topic)){
            return false;
        }
        if(topic.endsWith(ACK_FLAG)){
            return true;
        }
       return false;
    }


    protected   boolean isNeedAck(Message message){
        MqttMessage mqttMessage = message.getBuzMessage();
        int cc = mqttMessage.getHead().getCc();
        return 1==cc;
    }


    protected  <R extends AbstractMessage> R parseMessageBody(M message){
        throw new UnsupportedOperationException("不支持该操作");
    }

    protected abstract String getEventCode();

}
