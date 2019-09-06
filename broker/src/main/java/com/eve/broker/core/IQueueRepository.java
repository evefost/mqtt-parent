package com.eve.broker.core;

import java.util.Queue;

public interface IQueueRepository {

    Queue<SessionRegistry.EnqueuedMessage> createQueue(String cli, boolean clean);
}
