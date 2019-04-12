package com.xie.mqtt.common.handler;

import com.xie.mqtt.common.POINT;
import com.xie.mqtt.common.cmd.MockCmd;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统定义的处理器
 * @author xie
 */
public abstract class AbstactSystemHandler extends AbstractMqttPublishHandler {


    protected  final static ExecutorService service = Executors.newFixedThreadPool(2);

    protected volatile AtomicInteger loopTimes = new AtomicInteger(0);

    protected volatile boolean stop =true;


    @Override
    public boolean support(Object object) {
        return super.support(object);
    }


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

    @Override
    public POINT getPoint() {
        return POINT.SERVER;
    }
}
