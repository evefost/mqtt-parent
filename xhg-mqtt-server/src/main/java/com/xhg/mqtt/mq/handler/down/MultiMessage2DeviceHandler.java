package com.xhg.mqtt.mq.handler.down;


import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.Message;
import com.xhg.mqtt.mq.message.ReceiveRange;
import com.xhg.mqtt.mq.message.RocketMqMessage;
import com.xhg.mqtt.mq.message.RocketWrapperMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理下发设备多条消息
 *
 * @author xie
 */
public class MultiMessage2DeviceHandler extends ServiceNotifyHandler {

    public MultiMessage2DeviceHandler(MessageClient client) {
        super(client);
    }

    @Override
    public boolean support(Object message) {
        if (!super.support(message)) {
            return false;
        }
        if(message instanceof Message){
            Message msg = (Message) message;
            RocketMqMessage srcMessage = (RocketMqMessage) msg.getSrcMessage();
            ReceiveRange receiveRange = srcMessage.getReceiveRange();
            if(receiveRange == null){
                return false;
            }
            List<String> devices = receiveRange.getDevices();
            if (devices != null && !devices.isEmpty()) {
                return true;
            }

        }

        return false;
    }




    @Override
    protected <TM extends Message> void doProcess(TM message) {
        RocketWrapperMessage msg = (RocketWrapperMessage) message;
        List<String> topics = convert2MqttTopic(msg);
        if(logger.isDebugEnabled()){
            logger.debug("处理下发多个设备信息:{}",topics);
        }
        RocketWrapperMessage deviceMsg =  null;
        for(String topic:topics){
            deviceMsg = new RocketWrapperMessage();
            deviceMsg.setTopic(topic);
            deviceMsg.setMqttPayload(message.getMqttPayload());
            client.publish(deviceMsg);
        }
    }


    /**
     * 转成
     */
    private List<String> convert2MqttTopic(RocketWrapperMessage message) {
        RocketMqMessage srcMessage = message.getSrcMessage();
        List<String> devices = srcMessage.getReceiveRange().getDevices();
        List<String> topics = new ArrayList<>(devices.size());
        String eventCode = getEventCode();
        for (String deviceId : devices) {
            String topic = "/productKey/device/" + eventCode + "/single/" + deviceId;
            topics.add(topic);
        }
        return topics;
    }


}
