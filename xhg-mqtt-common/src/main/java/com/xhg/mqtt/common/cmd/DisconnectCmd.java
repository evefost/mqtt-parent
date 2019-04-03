package com.xhg.mqtt.common.cmd;

import com.alibaba.fastjson.JSON;

/**
 * 断开客户端连接数
 * @author xie
 */
public class DisconnectCmd extends MockCmd {

    /**
     * 启停(2匀速断开,3随机增加 用到该值)
     */
    private  volatile boolean start;

    /**
     * 模拟方式(1指定断开数,2匀速断开,3连续随机机断开)
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
        return "模拟断开客户端";
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
