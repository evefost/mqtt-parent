package com.eve.mqtt.client.command;


import com.eve.mqtt.client.RemotingContext;

public interface CommandHandler {

    int commandCode();

    void handleCommand(RemotingContext ctx, Object msg) throws Exception;

}
