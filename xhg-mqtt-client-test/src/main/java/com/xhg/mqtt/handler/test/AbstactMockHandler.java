package com.xhg.mqtt.handler.test;

import com.xhg.mqtt.common.bo.MockCmd;
import com.xhg.mqtt.handler.AbstractMqttPublishHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstactMockHandler extends AbstractMqttPublishHandler {

    protected  final static ExecutorService service = Executors.newFixedThreadPool(2);

    protected volatile AtomicInteger loopTimes = new AtomicInteger(0);

    protected volatile boolean stop =true;

    protected void handleCmd(MockCmd cmd,Runnable task){
        if (cmd.isStart()&& stop) {
            logger.info("启动{}... [{}]",cmd.getDescription(),cmd);
            stop = false;
            service.submit(task);
        } else if(cmd.isStart()){
            loopTimes.set(0);
            logger.info("更新{}... [{}].",cmd.getDescription(),cmd);
        }else {
            logger.info("停止{}...",cmd.getDescription());
            stop = true;
        }
    }
}
