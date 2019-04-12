package com.xie.mqtt.conf;

import com.xie.mqtt.metrics.BytesMetrics;
import com.xie.mqtt.metrics.MetricsMqtt;
import com.xie.mqtt.mq.PublisherListener;
import com.xie.mqtt.mq.SessionManager;
import io.moquette.BrokerConstants;
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
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${mqtt.port:1883}")
    private String port;

    @Value("${mqtt.host:0.0.0.0}")
    private String host;

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

    @Bean
    PublisherListener getPublisherListener(){
        return new PublisherListener();
    }

    public void startBroker() throws InterruptedException, IOException {
        IResourceLoader classpathLoader = new ClasspathResourceLoader();
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
        List<? extends InterceptHandler> userHandlers = Collections.singletonList(getPublisherListener());
        MqttListener mqttListener = applicationContext.getBean(MetricsMqtt.class);
        broker.setMqttListener(mqttListener);
        BytesMetrics bytesMetrics = applicationContext.getBean(BytesMetrics.class);
        broker.setBytesListener(bytesMetrics);
        classPathConfig.setProperty(BrokerConstants.PORT_PROPERTY_NAME, port);
        classPathConfig.setProperty(BrokerConstants.HOST_PROPERTY_NAME, host);
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