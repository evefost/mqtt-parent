package com.xhg.mqtt.mq;


import com.xhg.mqtt.mq.client.AbstractMessageClient;
import com.xhg.mqtt.mq.client.MessageClient;
import com.xhg.mqtt.mq.client.MqttConfig;
import com.xhg.mqtt.mq.handler.AbstractHandler;
import com.xhg.mqtt.mq.handler.down.BroadcastMessage2DeviceHandler;
import com.xhg.mqtt.mq.handler.down.MultiMessage2DeviceHandler;
import com.xhg.mqtt.mq.handler.up.BoxInfoHandler;
import com.xhg.mqtt.mq.handler.up.DeviceAddHandler;
import com.xhg.mqtt.mq.handler.up.DeviceConnectHandler;
import com.xhg.mqtt.mq.handler.up.DeviceDeleteHandler;
import com.xhg.mqtt.mq.handler.up.DeviceLoginHandler;
import com.xhg.mqtt.mq.handler.up.DeviceWillHandler;
import com.xhg.mqtt.mq.handler.up.SiteInfoHandler;
import java.util.List;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttClientAutoConfig implements SmartInitializingSingleton {

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private List<MessageInputListener> inputListeners;

    @Autowired
    private List<MessageOutputListener> outputListeners;

    @Autowired
    private List<MessageFailedListener> failedListeners;

    @Override
    public void afterSingletonsInstantiated() {
        //注册消息监听器
        for (MessageInputListener listener : inputListeners) {
            AbstractHandler.registerListner(listener);
        }
        for (MessageOutputListener listener : outputListeners) {
            AbstractMessageClient.registerListner(listener);
        }

        for (MessageFailedListener listener : failedListeners) {
            AbstractMessageClient.registerFailedListner(listener);
        }

    }



    @Bean
    BoxInfoHandler boxInfoHandler(@Qualifier(value = "rocketMqClient") MessageClient client) {
        return new BoxInfoHandler(client);
    }

    @Bean
    DeviceAddHandler deviceAddHandler(@Qualifier(value = "rocketMqClient") MessageClient client) {
        return new DeviceAddHandler(client);
    }

    @Bean
    DeviceLoginHandler deviceLoginHandler(@Qualifier(value = "rocketMqClient") MessageClient client) {
        return new DeviceLoginHandler(client);
    }

    @Bean
    DeviceDeleteHandler deviceDeleteHandler(@Qualifier(value = "rocketMqClient") MessageClient client) {
        return new DeviceDeleteHandler(client);
    }

    @Bean
    DeviceWillHandler deviceWillHandler(@Qualifier(value = "rocketMqClient") MessageClient client) {
        return new DeviceWillHandler(client);
    }

    @Bean
    SiteInfoHandler siteInfoHandler(@Qualifier(value = "rocketMqClient") MessageClient client) {
        return new SiteInfoHandler(client);
    }

    @Bean
    DeviceConnectHandler deviceConnectHandler(@Qualifier(value = "rocketMqClient") MessageClient client) {
        return new DeviceConnectHandler(client);
    }


    @Bean
    BroadcastMessage2DeviceHandler broadcastMessage2DeviceHandler(
        @Qualifier(value = "mqttClient") MessageClient client) {
        return new BroadcastMessage2DeviceHandler(client);
    }

    @Bean
    MultiMessage2DeviceHandler multiMessage2DeviceHandler(@Qualifier(value = "mqttClient") MessageClient client) {
        return new MultiMessage2DeviceHandler(client);
    }


}
