package com.eve.mqtt.common;

/**
 * 系统命令,主要测试用
 * @author xie
 */
public enum SystemCmd {

    /**
     * 测试广播
     */
    TEST_BROADCAST(Constants.SYSTEM_CONTROL_PATTERN + "/broadcast"),

    /**
     * 关闭所有客户端
     */
    TEST_DISCONNECT_CLIENT(Constants.SYSTEM_CONTROL_PATTERN + "/disconnect/client"),

    /**
     * 重置客户端
     */
    TEST_RESET_CLIENT(Constants.SYSTEM_CONTROL_PATTERN + "/reset/client"),

    /**
     * 增加客户端连接
     */
    TEST_INCREASE_CLIENT(Constants.SYSTEM_CONTROL_PATTERN + "/increase/client"),

    /**
     * 模拟客户端发送消息
     */
    TEST_MOCK_MSG(Constants.SYSTEM_CONTROL_PATTERN + "/mock/msg");



    String topic;

     SystemCmd(String topic){
        this.topic = topic;
    }

    public String getTopic(){
        return topic;
    }
}
