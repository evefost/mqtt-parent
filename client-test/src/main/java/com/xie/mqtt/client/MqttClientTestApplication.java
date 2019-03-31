package com.xie.mqtt.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xie"})
public class MqttClientTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqttClientTestApplication.class, args);
	}

}
