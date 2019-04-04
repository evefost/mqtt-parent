package com.xhg.mqtt.common.cmd;

import com.alibaba.fastjson.JSON;

/**
 * 模拟消息发送
 */
public class MockMsgCmd extends   MockCmd {

    /**
     * 启停
     */
   private  volatile boolean start;

    /**
     *模拟方式(0匀速发送,1周期内随机发送,2增速发送)
     */
   private volatile int type;


    /**
     * 增速发送 步长
     */
    private volatile long stepMilliseconds;

    @Override
    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getDescription() {
        return "模拟消息发送";
    }

    public void setType(int type) {
        this.type = type;
    }


    public long getStepMilliseconds() {
        return stepMilliseconds;
    }

    public void setStepMilliseconds(long stepMilliseconds) {
        this.stepMilliseconds = stepMilliseconds;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
