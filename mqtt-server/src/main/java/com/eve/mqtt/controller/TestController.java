package com.eve.mqtt.controller;


import com.eve.broker.core.Session;
import com.eve.broker.core.SessionRegistry;
import com.eve.mqtt.common.SystemCmd;
import com.eve.mqtt.common.cmd.DisconnectCmd;
import com.eve.mqtt.common.cmd.IncreaseCmd;
import com.eve.mqtt.common.cmd.MockMsgCmd;
import com.eve.mqtt.common.cmd.ResetCmd;
import com.eve.mqtt.mq.SessionManager;
import com.eve.mqtt.pojo.Response;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 测试用
 * @author xie
 */
@Api(tags = "设备模拟")
@RestController
@RequestMapping("/test")
public class TestController {

    protected static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private SessionManager sessionManager;

    private AtomicInteger messageId = new AtomicInteger(0);


    /**
     * 关闭指定客户端
     * @param clientId
     * @return
     */
    @ApiOperation(value = "关闭指定客户端")
    @PostMapping("/close/client")
    Response<Boolean> closeClient(String clientId) {
        if(StringUtils.isEmpty(clientId)){
            return Response.failue("clientId 不能为空");
        }
        SessionRegistry sessionRegistry = sessionManager.getSessionRegistry();
        ConcurrentMap<String, Session> sessions = sessionRegistry.getSessions();
        Session session = sessions.get(clientId);
        if(session == null){
            return Response.success(true);
        }
        session.closeImmediately();
        return Response.success(true);
    }

    /**
     * 测试广播消息
     * @return
     */
    @ApiOperation(value = "广播消息")
    @PostMapping("/broadcast")
    Response<Boolean> broadcast(String message) {
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(SystemCmd.TEST_BROADCAST.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(message.getBytes(UTF_8))).build();
        sessionManager.publish(publish);
       return Response.success(true);
    }

    /**
     * 通知客户端，重置所有或指定连接数(重置客户端不会自动重连)
     * @return
     */
    @ApiOperation(value = "重置所有或指定连接数")
    @PostMapping("/reset/clients")
    boolean resetClients(ResetCmd cmd) {

        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(SystemCmd.TEST_RESET_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(cmd.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;
    }


    /**
     * 通知客户端，主动关闭部分或全部客户端(将自动重连)
     * @return
     */
    @ApiOperation(value = "主动关闭部分或全部客户端(将自动重连)")
    @PostMapping("/disconnect/clients")
    boolean disconnect(DisconnectCmd cmd) {
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .topicName(SystemCmd.TEST_DISCONNECT_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(cmd.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;

    }

    /**
     * 通知客户端，主动增加连接数
     * @return
     */
    @ApiOperation(value = "主动增加连接数")
    @PostMapping("/increase/clients")
    boolean increaseClients(IncreaseCmd cmd) {
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .messageId(messageId.incrementAndGet())
            .topicName(SystemCmd.TEST_INCREASE_CLIENT.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(cmd.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;
    }

    /**
     * 模拟消息发送
     * @return
     */
    @ApiOperation(value = "模拟消息发送")
    @PostMapping("/mock/msg")
    boolean increaseClients(MockMsgCmd cmd) {
        logger.info("模拟消息发送:{}",cmd);
        if(cmd.getType()==3&& cmd.getStepMilliseconds()<=0){
            return false;
        }
        MqttPublishMessage publish = MqttMessageBuilders.publish()
            .messageId(messageId.incrementAndGet())
            .topicName(SystemCmd.TEST_MOCK_MSG.getTopic())
            .retained(false)
            .qos(MqttQoS.AT_MOST_ONCE)
            .payload(Unpooled.copiedBuffer(cmd.toString().getBytes(UTF_8))).build();
        sessionManager.publish(publish);
        return true;
    }





}
