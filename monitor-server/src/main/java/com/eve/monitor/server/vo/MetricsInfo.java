package com.eve.monitor.server.vo;

import com.eve.monitor.server.Constants;
import com.eve.monitor.server.MetricsType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xie
 */
public class MetricsInfo {

    private String name;
    private String value;

    private MetricsType type;


    private List<Tag> tags = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public MetricsType getType() {
        return type;
    }

    public void setType(MetricsType type) {
        this.type = type;
    }

    public static MetricsInfo build(String name,String value,MetricsType type){
        MetricsInfo metericInfo = new MetricsInfo();
        metericInfo.setName(name);
        metericInfo.setValue(value);
        metericInfo.setType(type);
        return metericInfo;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(type == MetricsType.ITEM){
            sb.append(name);
            if(tags.size()>0){
                sb.append(Constants.BRACE_LEFT);
                for(Tag t:tags){
                    sb.append(t.getKey());
                    sb.append("=");
                    sb.append(t.getValue());
                    sb.append(",");
                }
                sb.append(Constants.BRACE_RIGHT);
            }
            sb.append(" ").append(value);
        }else {
            sb.append(name).append(value);
        }
        return sb.toString();
    }
}
