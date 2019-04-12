package com.xie.monitor.server.service.impl;

import static com.xie.monitor.server.Constants.APP_INFO;
import static com.xie.monitor.server.Constants.BR;
import static com.xie.monitor.server.Constants.BRACE_LEFT;
import static com.xie.monitor.server.Constants.BRACE_RIGHT;
import static com.xie.monitor.server.Constants.DES_START;

import com.xie.monitor.server.Constants;
import com.xie.monitor.server.MetricsType;
import com.xie.monitor.server.service.AppManagerService;
import com.xie.monitor.server.service.PrometheusService;
import com.xie.monitor.server.vo.AppListVo;
import com.xie.monitor.server.vo.AppListVo.ApplicationsBean;
import com.xie.monitor.server.vo.AppListVo.ApplicationsBean.ApplicationBean;
import com.xie.monitor.server.vo.AppListVo.ApplicationsBean.ApplicationBean.InstanceBean;
import com.xie.monitor.server.vo.MetricsInfo;
import com.xie.monitor.server.vo.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class PrometheusServiceImpl implements PrometheusService,InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AppManagerService appManagerService;

    @Value("${monitor.env:dev}")
    private String monitorEnv;

    @Value("${monitor.query.size:50}")
    private int querySize;

    @Value("${monitor.app.ids:}")
    private String monitorAppIds;


    private static final AtomicInteger queryCount = new AtomicInteger(0);

    private static Set<String> monitorApps = new HashSet<>();


    ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public String queryInstanceMonitorInfo() throws InterruptedException {

        List<InstanceBean> instanceBeans = BatchTask.nextBatch(appManagerService,querySize);
        if(instanceBeans.isEmpty()){
            return "#may no apps be monitor "+monitorApps.toString();
        }
        List<MetricsInfo> list = new ArrayList<>();
//        instanceBeans.parallelStream().forEach(instanceBean -> {
//            List<MetricsInfo> itemList = parseOneInstance(instanceBean);
//            list.addAll(itemList);
//        });
        CountDownLatch latch = new CountDownLatch(instanceBeans.size());
        instanceBeans.forEach(instanceBean -> {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    List<MetricsInfo> itemList = parseOneInstance(instanceBean);
                    list.addAll(itemList);
                    latch.countDown();
                }
            });
        });

        latch.await();
        StringBuilder sb = new StringBuilder();
        list.forEach(metricsInfo -> {
            if(metricsInfo!= null){
                sb.append(metricsInfo.toString()).append(BR);
            }
        });
        return sb.toString();

    }

    @Override
    public boolean addMonitApp(String appName) {
        return monitorApps.add(appName.toUpperCase());
    }

    @Override
    public boolean removeMonitApp(String appName) {
        return monitorApps.remove(appName.toUpperCase());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] split = monitorAppIds.split(",");
        for(String appId:split){
            monitorApps.add(appId.toUpperCase());
        }
    }


    static class BatchTask {

        private static volatile Iterator<List<InstanceBean>> iterator;

        private BatchTask(){

        }
        public synchronized static List<InstanceBean> nextBatch(AppManagerService service,int batchSize) {
            if (iterator != null && iterator.hasNext()) {
                return iterator.next();
            } else {
                queryCount.incrementAndGet();
                List<InstanceBean> instanceBeans = loadAllInstances(service,batchSize);
                if(instanceBeans.isEmpty()){
                    return instanceBeans;
                }
               return nextBatch(service,batchSize);
            }
        }

        private static List<InstanceBean> loadAllInstances(AppManagerService service,int batchSize) {
            AppListVo appListVo = service.queryApps();
            ApplicationsBean application = appListVo.getApplications();
            List<ApplicationBean> applications = application.getApplication();
            List<ApplicationBean> applicationBeans = skipApp(applications);
            List<InstanceBean> instanceBeans = new ArrayList<>();
            applicationBeans.parallelStream().forEach(a -> {
                a.getInstance().stream().forEach(i -> {
                    if(monitorApps.contains(a.getName())){
                        i.setAppName(a.getName());
                        i.setIntanceCount(a.getInstance().size());
                        instanceBeans.add(i);
                    }
                });
            });
            if(instanceBeans.isEmpty()){
                return new ArrayList<InstanceBean>();
            }
            //切割分批处理
            List<List<InstanceBean>> instances = new ArrayList<>();
            List<InstanceBean> subList= null;
            for(int i=0;i<instanceBeans.size();i++){
                if(i%batchSize==0){
                    subList = new ArrayList<>();
                    instances.add(subList);
                }
                subList.add(instanceBeans.get(i));
            }
            iterator = instances.iterator();
            return subList;
        }

        private static List<ApplicationBean> skipApp(List<ApplicationBean> applications) {
            List<ApplicationBean> newList = new ArrayList<>();
            for (ApplicationBean app : applications) {
                if (app.getName().toLowerCase().contains("eureka")) {
                    continue;
                }
                newList.add(app);
            }
            return newList;
        }
    }


    private List<MetricsInfo> parseOneInstance(InstanceBean nextInstance) {
        long l = System.currentTimeMillis();
        String ipAddr = nextInstance.getIpAddr();
        int port = nextInstance.getPort().get$();
        String contextPath = nextInstance.getMetadata().get("contextPath");
        if(contextPath.contains("${")){
            logger.warn("被监控的应用[{}] contextPath[{}]有问题",nextInstance.getAppName(),contextPath);
            return new ArrayList<>();
        }
        String requestUri = "http://" + ipAddr + ":" + port + contextPath + "/prometheus";

        List<MetricsInfo> metricsInfos = new ArrayList<>();
        MetricsInfo appInfo = MetricsInfo.build("app_instance_info", "1", MetricsType.ITEM);
        metricsInfos.add(appInfo);
        appInfo.setName(APP_INFO);
        appInfo.addTag(Tag.buildWithMarks("env", monitorEnv));
        appInfo.addTag(Tag.buildWithMarks("application", nextInstance.getAppName()));
        appInfo.addTag(Tag.buildWithMarks("context_path", contextPath));
        MetricsInfo instance_count = MetricsInfo
            .build("app_instance_count", String.valueOf(nextInstance.getIntanceCount()), MetricsType.ITEM);
        metricsInfos.add(instance_count);
        logger.info("appName:{}/{}", nextInstance.getAppName(), requestUri);
        try {
            ResponseEntity<String> forEntity = restTemplate.getForEntity(requestUri, String.class);
            appInfo.setValue("1");
            List<MetricsInfo> uppack = unPackMetricsInfo(forEntity.getBody());
            metricsInfos.addAll(uppack);
        } catch (HttpClientErrorException clientEx) {
            appInfo.setValue(String.valueOf(clientEx.getRawStatusCode()));
        } catch (Throwable throwable) {
            appInfo.setValue("0");
        }

        Tag tag = Tag.buildWithMarks(Constants.INSTANCE, ipAddr + ":" + port);
        for (MetricsInfo f : metricsInfos) {
            f.addTag(tag);
        }
        return metricsInfos;
    }


    private List<MetricsInfo> unPackMetricsInfo(String s) {
        List<MetricsInfo> list = new ArrayList<>();
        String[] lines = s.split(BR);
        for (String line : lines) {
            if (line.startsWith(DES_START)) {
                continue;
            }
            MetricsInfo metricsInfo = new MetricsInfo();
            metricsInfo.setType(MetricsType.ITEM);
            parsLine(line, metricsInfo);
            list.add(metricsInfo);
        }
        return list;
    }


    private MetricsInfo parsLine(String line, MetricsInfo metricsInfo) {
        int spaceIndex = line.lastIndexOf(Constants.SPACE);
        String firstPart = line.substring(0, spaceIndex);
        String value = line.substring(spaceIndex, line.length());
        if (firstPart.contains(Constants.BRACE_RIGHT)) {
            //有tag
            //jvm_threads_peak{instance_id="10.10.13.146:8001",context_path="/",}  104.0
            String name = firstPart.substring(0, firstPart.indexOf(BRACE_LEFT));
            metricsInfo.setName(name);
            //解释tags
            String tagsStr = firstPart.substring(firstPart.indexOf(BRACE_LEFT) + 1, firstPart.indexOf(BRACE_RIGHT));
            String[] tag = tagsStr.split(",");
            for (String t : tag) {
                String[] kv = t.split("=");
                if (kv.length < 2) {
                    continue;
                }
                Tag tg = new Tag(kv[0], kv[1]);
                metricsInfo.addTag(tg);
            }
        } else {
            //无tag
            metricsInfo.setName(firstPart);
        }
        metricsInfo.setValue(value);
        return metricsInfo;
    }


}
