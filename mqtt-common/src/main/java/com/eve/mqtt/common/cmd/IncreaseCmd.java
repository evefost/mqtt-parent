package com.eve.mqtt.common.cmd;

import com.alibaba.fastjson.JSON;

/**
 * 增加客户端数量策略
 * @author xie
 */
public class IncreaseCmd extends MockCmd {

    /**
     * 启停(1匀速增加,2随机增加 用到该值)
     */
    private  volatile boolean start;

    /**
     * 模拟方式(0指定增加数,1匀速增加,2随机增加)
     */
    private volatile int type;

    /**
     * 每次增加数({@link #type}1指定数量，1匀速时作为步长，2随机是在该范围的随机)
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
