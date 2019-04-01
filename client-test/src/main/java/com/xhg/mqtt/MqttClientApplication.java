package com.xhg.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xhg"})
public class MqttClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqttClientApplication.class, args);
	}

}
