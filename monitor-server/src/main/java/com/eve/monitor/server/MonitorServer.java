package com.eve.monitor.server;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author xie
 */
@SpringBootApplication(scanBasePackages = {"com.xie"})
@EnableFeignClients(basePackages = {"com.xie.**"})
@SpringCloudApplication
@RestController
@EnableScheduling
public class MonitorServer extends SpringBootServletInitializer implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MonitorServer.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("spring boot 启动完成！");
    }





}
