package com.eve.mqtt.controller;

import lombok.Data;

import java.util.List;

/**
 *
 * @author xieyang
 * @date 19/3/31
 */
@Data
public class SessionClient {

   private String clientId;

    private long connectStart;

   private List<String> topics;


}
