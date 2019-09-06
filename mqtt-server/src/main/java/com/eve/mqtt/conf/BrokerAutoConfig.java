package com.eve.mqtt.conf;

import com.eve.broker.BrokerConstants;
import com.eve.broker.core.Server;
import com.eve.broker.core.SessionRegistry;
import com.eve.broker.core.config.ClasspathResourceLoader;
import com.eve.broker.core.config.IConfig;
import com.eve.broker.core.config.IResourceLoader;
import com.eve.broker.core.config.ResourceLoaderConfig;
import com.eve.broker.core.listener.MqttListener;
import com.eve.broker.interception.InterceptHandler;
import com.eve.mqtt.metrics.BytesMetrics;
import com.eve.mqtt.metrics.MetricsMqtt;
import com.eve.mqtt.mq.PublisherListener;
import com.eve.mqtt.mq.SessionManager;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
