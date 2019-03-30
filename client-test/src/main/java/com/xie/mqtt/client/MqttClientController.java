package com.xie.mqtt.client;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by xieyang on 19/3/30.
 */
@RestController
@RequestMapping("client")
public class MqttClientController {

    List<MqttClient> clients = new ArrayList<MqttClient>();

    private AtomicInteger clientCount = new AtomicInteger(0);

    @GetMapping("/create")
    String createClient(int count) throws MqttException {


        for (int i = 0; i < count; i++) {

            MqttClient client = null;
            try {
                Thread.sleep(20L);
                client = new MqttClient("tcp://localhost:1883", "device-" + clientCount.incrementAndGet(), new MemoryPersistence());

                client.setCallback(new MessageCallback(client));
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                client.connect(options);
                String singleTopic = "/topic/"+clientCount.get();
                String bordcastTopic = "/topic/all";
                String[] topics = {singleTopic,bordcastTopic};
                client.subscribe(topics);
                clients.add(client);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return clients.size() + "";
    }

    Random r = new Random();

    @GetMapping("/send")
    String sendMesage(String topic) throws MqttException {
        MqttClient mqttClient = clients.get(r.nextInt(clients.size()));
        mqttClient.publish("/topic/"+topic, "Test my payload".getBytes(UTF_8), 0, false);

        return "send success";
    }

    @GetMapping("/send2")
    String sendMesage2(String topic,int count) throws MqttException {
        MqttClient mqttClient = clients.get(r.nextInt(clients.size()));
        for(int i=0;i<count;i++){
            mqttClient.publish("/topic/"+topic, "Test my payload".getBytes(UTF_8), 0, false);

        }
        return "send success";
    }


    private class MessageCallback implements MqttCallbackExtended {
        private Logger logger = LoggerFactory.getLogger(MessageCallback.class);

        private MqttClient client;

        public MessageCallback(MqttClient client
        ) {
            this.client = client;
        }

        @Override
        public void connectionLost(Throwable cause) {
            logger.info("连接已断开:[{}]", client.getClientId());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            logger.info("收到消息[{}][{}]", client.getClientId(),topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            logger.info("发送完成");
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            logger.info("连接完成[{}] [{}]", client.getClientId(), reconnect);
        }
    }

}
