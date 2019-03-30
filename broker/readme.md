
##一、broker 处理客户端基本流程
###1.连接通道激活
创建一个通道连接信息(MQTTConnection)
NewNettyMQTTHandler#channelActive
###2.建立连接(CONNECT)
收到客户端CONNECT命令，从channel获取上面已经创建的
MQTTConnection#processConnect()处理CONNECT类型的信息
注册客户端连接信息SessionRegistry#bindToSession()
### 3.接收主题订阅(SUBSCRIBE)
MQTTConnection#processSubscribe()
处理订阅
###4.接收主题发布()
MQTTConnection#processPublish()





二、消息处理
1.NewNettyMQTTHandler（消息处理器）

2.MQTTConnection（channel消息处理者）
handleMessage()据消息类型分发处理
processPublish 解释消息qos ，topic ，

3.PostOffice(消息路由相关)
publish2Subscribers()发布到订阅者端

4.ISubscriptionsDirectory
matchQosSharpening(topic)获取匹配的订阅者
Subscriptio 订阅者信息，包含clientId,qos,topc过滤器

