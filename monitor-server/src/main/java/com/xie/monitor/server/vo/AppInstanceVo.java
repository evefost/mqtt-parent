package com.xie.monitor.server.vo;


import com.xie.monitor.server.vo.AppListVo.ApplicationsBean.ApplicationBean.InstanceBean;

/**
 * @author xie yang
 * @date 2018/11/6-14:14
 */
public class AppInstanceVo {

    private InstanceBean instance;

    public InstanceBean getInstance() {
        return instance;
    }

    public void setInstance(InstanceBean instance) {
        this.instance = instance;
    }
}
