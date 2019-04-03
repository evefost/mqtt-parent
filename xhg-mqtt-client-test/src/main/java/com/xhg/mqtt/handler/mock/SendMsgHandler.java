package com.xhg.mqtt.handler.mock;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.sun.javafx.UnmodifiableArrayList;
import com.xhg.mqtt.common.SystemCmd;
import com.xhg.mqtt.common.cmd.MockMsgCmd;
import com.xhg.mqtt.netty.MessageClient;
import com.xhg.mqtt.netty.MessageClientFactory;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/**
 * 启动模拟客户端发送消息
 *
 * @author xie
 */
@Component
public class SendMsgHandler extends AbstactMockHandler {

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
    protected <T extends MqttMessage> void doProcess(T message) {
        MqttPublishMessage mqttMessage = (MqttPublishMessage) message;
        cmd = decodeContent(mqttMessage, MockMsgCmd.class);

        handleCmd(cmd,new MockTask());


    }



    public class MockTask implements Runnable {

        private Random random = new Random();

        @Override
        public void run() {
            while (!stop) {
                switch (cmd.getType()) {
                    case 1:
                        average();
                        break;
                    case 2:
                        random();
                        break;
                    case 3:
                        increase();
                        break;
                    default:
                }
                doSendMsg();
            }
        }

        /**
         * 匀速发送
         */
        void average() {
            try {
                TimeUnit.SECONDS.sleep(cmd.getPeriodMilliseconds()/1000);
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
            long time = cmd.getPeriodMilliseconds()-cmd.getStepMilliseconds()*loopTimes.incrementAndGet();
            if(time<0){
                time = 10;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        protected void doSendMsg(){
            UnmodifiableArrayList<MessageClient> nettyChannels = MessageClientFactory.getClients();
            for (MessageClient client:nettyChannels){
                String mockData = "这是"+client.getClientId()+"模拟的消息";
                MqttPublishMessage publish = MqttMessageBuilders.publish()
                    .topicName("/client/mock")
                    .retained(false)
                    .qos(MqttQoS.AT_MOST_ONCE)
                    .payload(Unpooled.copiedBuffer(mockData.getBytes(UTF_8))).build();
                client.send(publish);
            }
        }
    }
}
