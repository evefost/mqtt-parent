package com.xhg.mqtt.common.bo;

import com.alibaba.fastjson.JSON;

public class ChangeClientNumber {

    private String description;

    private int count = 0;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
