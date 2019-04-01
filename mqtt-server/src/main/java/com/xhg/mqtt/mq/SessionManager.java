package com.xhg.mqtt.mq;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    public static ConcurrentHashMap<String,String> onlineClients = new ConcurrentHashMap<>();


    public static void add(String deviceId){
        onlineClients.put(deviceId,deviceId);
    }

    public static ConcurrentHashMap<String,String> getonlineClients(){
       return onlineClients;
    }

    public static void remove(String deviceId){
        onlineClients.remove(deviceId);
    }

    public static int getOnlineCount(){
        return onlineClients.size();
    }

}
