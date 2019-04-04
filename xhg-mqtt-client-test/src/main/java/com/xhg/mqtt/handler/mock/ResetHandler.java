package com.xhg.mqtt.handler.mock;

import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.common.cmd.ResetCmd;
import com.xhg.mqtt.netty.MessageClientFactory;
import io.netty.handler.codec.mqtt.MqttMessage;
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
public class ResetHandler extends AbstactMockHandler {

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
    protected <T extends MqttMessage> void doProcess(T message) {
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        cmd = decodeContent(mqttMessage, ResetCmd.class);
        handleCmd(cmd, new MockTask());
    }

    public class MockTask implements Runnable {

        private Random random = new Random();

        @Override
        public void run() {
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
