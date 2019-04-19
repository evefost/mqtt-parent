package com.xie.monitor.server.controller;

import com.xie.monitor.server.service.PrometheusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrometheusController {


    @Autowired
    PrometheusService prometheusService;


    @GetMapping(value = "/monitor/prometheus", produces = MediaType.TEXT_HTML_VALUE)
    String queryAppMonitorInfo() throws InterruptedException {
      return   prometheusService.queryInstanceMonitorInfo();
    }

    @PostMapping(value = "/monitor/add")
    boolean addApp(String appName) {
        return   prometheusService.addMonitApp(appName);
    }

    @PostMapping(value = "/monitor/delete")
    boolean delete(String appName) {
        return   prometheusService.removeMonitApp(appName);
    }





}
