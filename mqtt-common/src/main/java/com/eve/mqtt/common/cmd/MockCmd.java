package com.eve.mqtt.common.cmd;

/**
 * 增加客户端数理策略
 */
public abstract class MockCmd {

    /**
     * 时间周期
     */
    protected volatile long periodMilliseconds;

    public long getPeriodMilliseconds() {
        return periodMilliseconds;
    }

    public void setPeriodMilliseconds(long periodMilliseconds) {
        this.periodMilliseconds = periodMilliseconds;
    }

    public abstract boolean isStart();

    public abstract int getType();

    public abstract String getDescription();


}
