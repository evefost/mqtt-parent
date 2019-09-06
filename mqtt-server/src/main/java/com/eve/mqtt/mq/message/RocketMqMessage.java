package com.eve.mqtt.mq.message;

/**
 * @Name: RocketMQ推送协议  
 * @Description: TODO
 * @Copyright: Copyright (c) 2018   
 * @Author chenxiaojun  
 * @Create Date 2018年7月16日  
 * @Version 1.0.0
 */
public class RocketMqMessage extends RocketMqBaseMessage {
	/**
	 * 消息id
	 */
	private String messageId;
	/**
	 * 事件代码
	 */
	private String eventCode;
	/**
	 * 客户端标识
	 */
	private String clientKey;
	/**
	 * 客户端类型
	 */
	private String clientType;
	/**
	 * 范围
	 */
	private ReceiveRange receiveRange;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getClientKey() {
		return clientKey;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public ReceiveRange getReceiveRange() {
		return receiveRange;
	}

	public void setReceiveRange(ReceiveRange receiveRange) {
		this.receiveRange = receiveRange;
	}
}
