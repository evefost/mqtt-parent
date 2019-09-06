package com.xie.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
//@EnableDiscoveryClient
//@EnableScanTopic
public class MqttServerApplication {


    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(MqttServerApplication.class, args);

    }

}
