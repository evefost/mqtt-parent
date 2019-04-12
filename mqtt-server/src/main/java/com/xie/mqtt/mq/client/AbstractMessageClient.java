package com.xie.mqtt.mq.client;


import com.xie.mqtt.mq.listener.MessageFailedListener;
import com.xie.mqtt.mq.message.Message;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class AbstractMessageClient<M extends Message> implements MessageClient {

    protected Logger logger = LoggerFactory.getLogger(getClass());


    private static List<MessageFailedListener> failedListeners = new ArrayList<>();
    @Override
    public <M extends Message> void publish(M message) {
        if(logger.isDebugEnabled()){
            logger.debug("下发消息:{}",message.getTopic());
        }
        doBefore(message);
        doPublish(message);
        doAfter(message);
    }

    protected void onFailed(Throwable e,Message message){
        for (MessageFailedListener listener : failedListeners) {
            listener.onFailed(e,message);
        }
    }


    protected abstract void doPublish(Message message);


    protected void doBefore(Message message) {

    }

    private void doAfter(Message message) {

    }
    @Override
    public String getClientId() {
        return "default";
    }

    protected  abstract  <C> C choseClient();


    public static void registerFailedListner(MessageFailedListener listener) {
        failedListeners.add(listener);
    }

    public static void unRegisterFailedListener(MessageFailedListener listener) {
        failedListeners.remove(listener);
    }
}
