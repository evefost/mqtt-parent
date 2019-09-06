package com.eve.mqtt.handler.mock;

import com.eve.mqtt.client.MessageClient;
import com.eve.mqtt.client.MessageClientFactory;
import com.eve.mqtt.common.Constants;
import com.eve.mqtt.common.SystemCmd;
import com.eve.mqtt.common.cmd.MockMsgCmd;
import com.eve.mqtt.common.handler.AbstactSystemHandler;
import com.sun.javafx.UnmodifiableArrayList;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 启动模拟客户端发送消息
 *
 * @author xie
 */
@Component
public class SendMsgHandler extends AbstactSystemHandler {

    private volatile AtomicInteger loopTimes = new AtomicInteger(0);

    private MockMsgCmd cmd;

    @Override
    public boolean support(Object object) {
        if (object instanceof MqttPublishMessage) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) object;
            MqttPublishVariableHeader header = publishMessage.variableHeader();
            String topic = header.topicName();
            if (SystemCmd.TEST_MOCK_MSG.getTopic().equals(topic)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected <IM> void doProcess(IM message) {
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        cmd = decodeContent(mqttMessage, MockMsgCmd.class);

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
                            average();
                            break;
                        case 1:
                            random();
                            break;
                        case 2:
                            increase();
                            break;
                        default:
                    }
                    doSendMsg();
                }
            } catch (Throwable ex) {
                logger.warn("测试任务异常", ex);
            }
            stop = true;
        }

        /**
         * 匀速发送
         */
        void average() {
            try {
                TimeUnit.SECONDS.sleep(cmd.getPeriodMilliseconds() / 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        /**
         * 周期内随机发送
         */
        void random() {
            long time = random.nextInt((int) cmd.getPeriodMilliseconds());
            try {
                TimeUnit.MILLISECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 增速发送
         */
        void increase() {
            long time = cmd.getPeriodMilliseconds() - cmd.getStepMilliseconds() * loopTimes.incrementAndGet();
            if (time < 0) {
                time = 10;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        protected void doSendMsg() {
            UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getClients();
            for (MessageClient client : nettyChannels) {
                String mockData = "这是" + client.getClientId() + "模拟的消息";
                MqttPublishMessage publish = MqttMessageBuilders.publish()
                    .topicName(Constants.SYSTEM_CONTROL_PATTERN+"/client/mock")
                    .retained(false)
                    .qos(MqttQoS.AT_MOST_ONCE)
                    .payload(Unpooled.copiedBuffer(mockData.getBytes(UTF_8))).build();
                client.send(publish);
            }
        }
    }
}
