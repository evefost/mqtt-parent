package com.xie.mqtt.mq.message;

import java.io.Serializable;
import lombok.Data;

/**
 * @Name: rocketMQ协议基础数据（响应协议）  
 * @Description: TODO
 * @Copyright: Copyright (c) 2018   
 * @Author chenxiaojun  
 * @Create Date 2018年7月16日  
 * @Version 1.0.0
 */
@Data
public class RocketMqBaseMessage implements Serializable{
	/**
	 * Base64编码后的protobuf数据
	 */
	private String data;
	/**
	 * 签名
	 */
	private String sign;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
