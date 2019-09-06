package com.eve.mqtt.command;


import com.eve.mqtt.client2.RemotingContext;

public interface CommandHandler {

    int commandCode();

    void handleCommand(RemotingContext ctx, Object msg) throws Exception;

}
