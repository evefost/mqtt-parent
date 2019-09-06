package com.eve.mqtt.command;

import com.eve.mqtt.client2.RemotingContext;
import com.eve.mqtt.common.handler.HandlerDispatcher;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Administrator
 */
public class PublishMessageHandler implements CommandHandler {

    protected static final Logger logger = LoggerFactory.getLogger(PublishMessageHandler.class);

    private static AtomicInteger id = new AtomicInteger(0);

    @Override
    public int commandCode() {
        return MqttMessageType.PUBLISH.value();
    }

    @Override
    public void handleCommand(RemotingContext ctx, Object msg) throws Exception {
        HandlerDispatcher.process(msg);
    }
}
