/*
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package com.eve.mqtt.client2;

import com.eve.mqtt.command.CommandHandler;
import com.eve.mqtt.command.CommandManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ClientMqttHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientMqttHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        CommandHandler commandHandler = CommandManager.getCommandHandler(message);
        if (commandHandler != null) {
            RemotingContext remotingContext = new RemotingContext(ctx, false);
            commandHandler.handleCommand(remotingContext, message);
        } else {
            logger.debug("Received a message");
        }
    }

}
