package com.xhg.mqtt.mq.handler.up;


import static com.xhg.mqtt.common.EventCodeEnum.BOX_INFO;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.xhg.mqtt.common.proto.BoxInfoPb.BoxInfo;
import com.xhg.mqtt.common.proto.MqttMessagePb.MqttMessage;
import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.message.MqttWrapperMessage;


/**
 * @author xie
 */
public class BoxInfoHandler extends AbstractUpHandler {


    public BoxInfoHandler(MessageClient client) {
        super(client);
    }

    @Override
    public String getEventCode() {
        return BOX_INFO.getCode();
    }



    @Override
    protected void doProcess(MqttWrapperMessage message) {
        logger.debug("监听到设备上报box信息");
        message.setTopic("xhg-order-device");
        BoxInfo boxInfo = parseMessageBody(message);
        //logger.debug("消息内容{}",boxInfo);
        //client.publish(message);
    }



    @Override
    public BoxInfo parseMessageBody(MqttWrapperMessage message) {
        MqttMessage mqttMessage = message.getBuzMessage();
        ByteString body = mqttMessage.getBody();
        BoxInfo boxInfo = null;
        try {
            boxInfo = BoxInfo.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return boxInfo;
    }


}
