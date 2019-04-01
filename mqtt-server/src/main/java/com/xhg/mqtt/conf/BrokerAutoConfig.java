package com.xhg.mqtt.conf;

import com.xhg.mqtt.metrics.MetricsMqttListener;
import com.xhg.mqtt.mq.PublisherListener;
import com.xhg.mqtt.mq.SessionManager;
import io.moquette.broker.Server;
import io.moquette.broker.SessionRegistry;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import io.moquette.broker.listener.MqttListener;
import io.moquette.interception.InterceptHandler;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerAutoConfig implements ApplicationContextAware, SmartInitializingSingleton {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private Server broker;

    @Bean
    public Server getServer() {
        return new Server();
    }

    @Bean
    public SessionManager getSessionManager() {
        SessionManager sessionManager = new SessionManager();
        return sessionManager;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            startBroker();
        } catch (Throwable e) {
            logger.error("broker 启动失败:", e);
        }
    }

    public void startBroker() throws InterruptedException, IOException {
        IResourceLoader classpathLoader = new ClasspathResourceLoader();
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
        List<? extends InterceptHandler> userHandlers = Collections.singletonList(new PublisherListener());
        MqttListener mqttListener = applicationContext.getBean(MetricsMqttListener.class);
        broker.setMqttListener(mqttListener);
        broker.startServer(classPathConfig, userHandlers);
        //Bind  a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping broker");
            broker.stopServer();
            logger.info("Broker stopped");
        }));
        SessionRegistry sessionRegistry = broker.getSessionRegistry();
        sessionManager.setSessionRegistry(sessionRegistry);
        sessionManager.setSubscriptions(sessionRegistry.getsubscriptionsDirectory());
        logger.info("Broker start:");

    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
