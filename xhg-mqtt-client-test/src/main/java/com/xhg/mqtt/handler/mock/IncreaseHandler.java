package com.xhg.mqtt.handler.mock;


import com.xhg.mqtt.client.MessageClientFactory;
import com.xhg.mqtt.client.MqttNettyClient;
import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.common.cmd.IncreaseCmd;
import com.xhg.mqtt.common.handler.AbstactSystemHandler;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/**
 * 增加客户端连接
 *
 * @author xie
 */
@Component
public class IncreaseHandler extends AbstactSystemHandler {

    private IncreaseCmd cmd;

    @Override
    public boolean support(Object object) {
        if (object instanceof MqttPublishMessage) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) object;
            MqttPublishVariableHeader header = publishMessage.variableHeader();
            String topic = header.topicName();
            if (SystemCmd.TEST_INCREASE_CLIENT.getTopic().equals(topic)) {
                return true;
            }
        }
        return false;
    }



    @Override
    protected <IM> void doProcess(IM message) {
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        cmd = decodeContent(mqttMessage, IncreaseCmd.class);
        logger.info("收到增客户端命令count:{}", cmd.getCount());
        handleCmd(cmd, new MockTask());
    }


    public class MockTask implements Runnable {

        private Random random = new Random();

        @Override
        public void run() {
            try {
                while (!stop) {
                    switch (cmd.getType()) {
                        case 0:
                            increaseClients(cmd.getCount());
                            stop=true;
                            break;
                        case 1:
                            average();
                            break;
                        case 2:
                            random();
                            break;
                        default:
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(cmd.getPeriodMilliseconds());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable ex) {
                logger.warn("测试任务异常", ex);
            }

            stop = true;
        }

        /**
         * 匀速增加
         */
        void average() {
            increaseClients(cmd.getCount());
        }

        /**
         * count内随机增加
         */
        void random() {
            int randomCount = random.nextInt(cmd.getCount());
            increaseClients(randomCount);
        }


    }


    private void increaseClients(int count) {
        //创建中，所有请求都丢掉
        if (count == 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            try {
                MessageClientFactory.getAndCreateClient(MqttNettyClient.class, false);
            } catch (Exception e) {
                logger.error("创建连接异常", e);
            }
        }
    }
}
