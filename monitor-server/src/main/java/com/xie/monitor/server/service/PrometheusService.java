package com.xie.monitor.server.service;

/**
 * @author xie
 */
public interface PrometheusService {


    String queryInstanceMonitorInfo() throws InterruptedException;


    boolean addMonitApp(String appName);

    boolean removeMonitApp(String appName);

}
