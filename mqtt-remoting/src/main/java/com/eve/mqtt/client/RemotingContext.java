/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eve.mqtt.client;

import io.netty.channel.ChannelHandlerContext;


public class RemotingContext {

    private ChannelHandlerContext channelContext;

    private boolean serverSide = false;


    private boolean timeoutDiscard = true;


    private long arriveTimestamp;


    private int timeout;

    public RemotingContext(ChannelHandlerContext ctx, boolean serverSide) {
        this.channelContext = ctx;
        this.serverSide = serverSide;
    }

    public ChannelHandlerContext getChannelContext() {
        return channelContext;
    }

    public void setChannelContext(ChannelHandlerContext channelContext) {
        this.channelContext = channelContext;
    }

    public boolean isServerSide() {
        return serverSide;
    }

    public void setServerSide(boolean serverSide) {
        this.serverSide = serverSide;
    }

    public boolean isTimeoutDiscard() {
        return timeoutDiscard;
    }

    public void setTimeoutDiscard(boolean timeoutDiscard) {
        this.timeoutDiscard = timeoutDiscard;
    }

    public long getArriveTimestamp() {
        return arriveTimestamp;
    }

    public void setArriveTimestamp(long arriveTimestamp) {
        this.arriveTimestamp = arriveTimestamp;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
