package com.xie.mqtt.mq.message;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @Name: 指令接收范围，当某一级数据为空时，表示针对当级设备群发，如area为空，推送范围为 {eventCode}/{province}/{city}/+  
 * @Description: TODO
 * @Copyright: Copyright (c) 2018   
 * @Author chenxiaojun  
 * @Create Date 2018年7月16日  
 * @Version 1.0.0
 */
@Data
public class ReceiveRange implements Serializable{
	/**
	 * 省
	 */
	private String province;
	/**
	 * 市
	 */
	private String city;                       
	/**
	 * 区
	 */
	private String area;                       
	/**
	 * 接收设备列表
	 */
	private List<String> devices;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public List<String> getDevices() {
		return devices;
	}

	public void setDevices(List<String> devices) {
		this.devices = devices;
	}
}
