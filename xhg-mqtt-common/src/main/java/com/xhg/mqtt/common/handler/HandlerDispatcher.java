//package com.xhg.mqtt.common.handler;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 消息处理器
// *
// * @author xie
// */
//public class HandlerDispatcher {
//
//
//
//    private final static List<Handler> handlers = new ArrayList<>();
//
//    private  static AbstractHandler defaultHandler;
//
//    private ApplicationContext applicationContext;
//
//    public static <H extends AbstractHandler> H getHandler(Class<H> clazz) {
//        for (Handler handler : handlers) {
//            if (handler instanceof DeviceConnectHandler) {
//                return (H) handler;
//            }
//        }
//        return null;
//    }
//
//    public final static void process(Message message) {
//        boolean matchHandler = false;
//        for (AbstractHandler handler : handlers) {
//            if (handler.support(message)) {
//                matchHandler = true;
//                handler.processMessage(message);
//            }
//        }
//        if (!matchHandler) {
//            if(message instanceof MqttWrapperMessage){
//                defaultHandler.processMessage(message);
//            }else {
//                logger.warn("消息没配置到处理器");
//            }
//        }
//    }
//
//    @Override
//    public void afterSingletonsInstantiated() {
//        Map<String, AbstractHandler> beansOfType = applicationContext.getBeansOfType(AbstractHandler.class);
//        beansOfType.forEach(new BiConsumer<String, AbstractHandler>() {
//            @Override
//            public void accept(String key, AbstractHandler bean) {
//                if(bean instanceof DeviceDefaultHandler){
//                    defaultHandler= bean;
//                }else {
//                    handlers.add(bean);
//                }
//            }
//        });
//    }
//
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//}
