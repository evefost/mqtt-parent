package com.xie.mqtt.handler.mock;

import com.xie.mqtt.client.MessageClientFactory;
import com.xie.mqtt.common.SystemCmd;
import com.xie.mqtt.common.cmd.ResetCmd;
import com.xie.mqtt.common.handler.AbstactSystemHandler;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/**
 * 重置客户端连接
 * @author xie
 */
@Component
public class ResetHandler extends AbstactSystemHandler {

    private ResetCmd cmd;

    @Override
    public boolean support(Object object) {
        if (object instanceof MqttPublishMessage) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) object;
            MqttPublishVariableHeader header = publishMessage.variableHeader();
            String topic = header.topicName();
            if (SystemCmd.TEST_RESET_CLIENT.getTopic().equals(topic)) {
                return true;
            }
        }
        return false;
    }




    @Override
    protected <IM> void doProcess(IM message) {
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        cmd = decodeContent(mqttMessage, ResetCmd.class);
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
                            MessageClientFactory.reset(cmd.getCount());
                            stop = true;
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
            MessageClientFactory.reset(cmd.getCount());
        }

        /**
         * count内随机增加
         */
        void random() {
            int randomCount = random.nextInt(cmd.getCount());
            MessageClientFactory.reset(randomCount);
        }

    }

}