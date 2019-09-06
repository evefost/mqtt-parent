package com.eve.monitor.server.service;


import com.eve.monitor.server.vo.AppListVo;
import com.eve.monitor.server.vo.AppListVo.ApplicationsBean.ApplicationBean;
import com.eve.monitor.server.vo.Page;

/**
 * @author xie yang
 * @date 2018/11/8-11:12
 */
public interface AppManagerService {

    AppListVo queryApps();

    AppListVo queryAppsByName(String appID);

    Page<ApplicationBean> queryAppsByPage(String searchWord, int currentPage, int pageSize);
}
