package com.xhg.mqtt.mq.handler.down;


import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.ReceiveRange;
import com.xhg.mqtt.mq.message.RocketMqMessage;
import java.util.List;
import org.springframework.util.StringUtils;

/**
 * 处理下发设备广播消息
 *
 * @author xie
 */
public class BroadcastMessage2DeviceHandler extends ServiceNotifyHandler {


    public BroadcastMessage2DeviceHandler(MessageClient client) {
        super(client);
    }

    @Override
    public boolean support(Object message) {
        if (super.support(message)) {
            Message msg = (Message) message;
            RocketMqMessage srcMessage = (RocketMqMessage) msg.getSrcMessage();
            ReceiveRange receiveRange = srcMessage.getReceiveRange();
            if (receiveRange == null) {
                return true;
            }
            List<String> devices = receiveRange.getDevices();
            if (devices == null || devices.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected <TM extends Message> void doProcess(TM message) {

        RocketMqMessage srcMessage = (RocketMqMessage) message.getSrcMessage();
        ReceiveRange receiveRange = srcMessage.getReceiveRange();
        StringBuilder topic = new StringBuilder("/productKey/server");
        if (!StringUtils.isEmpty(receiveRange.getProvince())) {
            topic.append("/").append(receiveRange.getProvince());
        }
        if (!StringUtils.isEmpty(receiveRange.getCity())) {
            topic.append("/").append(receiveRange.getCity());
        }
        if (!StringUtils.isEmpty(receiveRange.getArea())) {
            topic.append("/").append(receiveRange.getArea());
        }
        if(logger.isDebugEnabled()){
            logger.debug("处理下发广播信息topic:{}",topic.toString());
        }
        message.setTopic(topic.toString());
        client.publish(message);
    }


}
