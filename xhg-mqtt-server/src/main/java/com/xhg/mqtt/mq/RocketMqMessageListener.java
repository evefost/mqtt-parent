package com.xhg.mqtt.mq;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.xhg.mqtt.common.POINT;
import com.xhg.mqtt.common.proto.MqttMessagePb;
import com.xhg.mqtt.mq.message.RocketMqMessage;
import com.xhg.mqtt.mq.message.RocketWrapperMessage;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

//import com.xhg.message.client.annotation.Consumer;
//import com.xhg.message.client.annotation.Topic;


/**
 * rocket mq消息监听
 */
//@Consumer
public class RocketMqMessageListener implements Decoder<RocketWrapperMessage> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MeterRegistry registry;


	/**
	 * 订阅广播指令
	 * chenxiaojun 2018年7月19日
	 */
//	@Topic("${spring.extend.mq.consumer.broadcast:}")
	public void subscribeBroadcastOrder(RocketMqMessage message) {
		RocketWrapperMessage msg = new RocketWrapperMessage();
		msg.setSrcMessage(message);
		msg.setFrom(POINT.SERVER);
		msg.setTo(POINT.CLIEN);
		decode(msg);
		HandlerManager.process(msg);
	}



	@Override
	public void decode(RocketWrapperMessage message) {
		RocketMqMessage srcMessage = message.getSrcMessage();
		byte[] bytes = Base64.decodeBase64(srcMessage.getData());
		message.setMqttPayload(bytes);
		try {
			MqttMessagePb.MqttMessage mqttMessage = MqttMessagePb.MqttMessage.parseFrom(message.getMqttPayload());
            message.setBuzMessage(mqttMessage);
            if (logger.isDebugEnabled()) {
				logger.debug("解码rocket mq 消息:{}", mqttMessage.getHead());
			}
		} catch (InvalidProtocolBufferException e) {
			logger.error("解码rocket mq消息失败:{}", JSON.toJSON(srcMessage), e);
		} catch (Throwable e) {
			logger.error("解码rocket mq消息失败:{}", JSON.toJSON(srcMessage), e);
		}
	}
}
