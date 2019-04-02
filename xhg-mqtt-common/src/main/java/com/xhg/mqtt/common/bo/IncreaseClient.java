package com.xhg.mqtt.common.bo;

import com.alibaba.fastjson.JSON;

public class IncreaseClient {

    private int count=0;

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
