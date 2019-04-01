package com.xhg.mqtt;

//import com.xhg.message.client.annotation.EnableScanTopic;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.xhg.mqtt"})
//@EnableDiscoveryClient
//@EnableScanTopic
public class MqttApplication {


    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(MqttApplication.class, args);

    }

}
