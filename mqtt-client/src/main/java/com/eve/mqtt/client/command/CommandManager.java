package com.eve.mqtt.client.command;

import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Administrator
 */
public class CommandManager {

    private static ConcurrentHashMap<Integer, CommandHandler> commandHandlers = new ConcurrentHashMap<>();

    {
        SendConnectHandler sendConnectHandler = new SendConnectHandler();
        SendSubscriptHandler subscriptHandler = new SendSubscriptHandler();
        PublishMessageHandler publishMessageHandler = new PublishMessageHandler();
        commandHandlers.put(sendConnectHandler.commandCode(), sendConnectHandler);
        commandHandlers.put(subscriptHandler.commandCode(), subscriptHandler);
        commandHandlers.put(publishMessageHandler.commandCode(), publishMessageHandler);
    }

    public static CommandHandler getCommandHandler(Object message) {
        if (message instanceof MqttMessage) {
            MqttMessage msg = (MqttMessage) message;
            MqttFixedHeader mqttFixedHeader = msg.fixedHeader();
            MqttMessageType mqttMessageType = mqttFixedHeader.messageType();
            int value = mqttMessageType.value();
            return commandHandlers.get(value);
        } else {
            return null;
        }
    }


}
