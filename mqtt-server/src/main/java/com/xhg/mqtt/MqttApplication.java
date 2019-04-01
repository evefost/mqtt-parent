package com.xhg.mqtt;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.interception.InterceptHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.xhg"})
public class MqttApplication {

    public static Server mqttBroker;

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(MqttApplication.class, args);
        startBroker(args);
    }

    public static void startBroker(String[] args) throws InterruptedException, IOException {
        IResourceLoader classpathLoader = new ClasspathResourceLoader();
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);

        mqttBroker = new Server();
        List<? extends InterceptHandler> userHandlers = Collections.singletonList(new PublisherListener());
        mqttBroker.startServer(classPathConfig, userHandlers);
        //Bind  a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping broker");
            mqttBroker.stopServer();
            System.out.println("Broker stopped");
        }));

        System.out.println("Broker start:");
    }

}
