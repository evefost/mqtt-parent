package com.xie.monitor.server.remote;



import com.xie.monitor.server.vo.AppInfoVo;
import com.xie.monitor.server.vo.AppInstanceVo;
import com.xie.monitor.server.vo.AppListVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xie yang
 * @date 2018/10/11-10:30
 */
@FeignClient(name = "xhg-eureka-server")
public interface EurekaFeignClient {

    /**
     * 设置服务状态
     */
    @PutMapping(value = "/eureka/apps/{appID}/{instanceID}/metadata")
    String updateServerStatus(@PathVariable("appID") String appID, @PathVariable("instanceID") String instanceID,
        @RequestParam("instance_status") String instance_status);


    @GetMapping(value = "/eureka/apps",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    AppListVo queryApps();

    @GetMapping(value = "/eureka/apps/{appID}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    AppInfoVo queryAppInstancesByAppId(@PathVariable("appID") String appID);


    @GetMapping(value = "/eureka/apps/{appID}/{instanceID}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    AppInstanceVo queryInstanceByInstanceId(@PathVariable("appID") String appID,
        @PathVariable("instanceID") String instanceID);
}
