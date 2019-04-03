package com.xhg.mqtt.common.bo;

import com.alibaba.fastjson.JSON;

/**
 * 增加客户端数量策略
 * @author xie
 */
public class IncreaseCmd extends MockCmd {

    /**
     * 启停(2匀速增加,3随机增加 用到该值)
     */
    private  volatile boolean start;

    /**
     * 模拟方式(1指定增加数,2匀速增加,3随机增加)
     */
    private volatile int type;

    /**
     * 每次增加数(1指定数量，2匀速时作为步长，3随机是在该范围的随机)
     */
    private volatile int count;

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
        return "模拟增加客户端";
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
