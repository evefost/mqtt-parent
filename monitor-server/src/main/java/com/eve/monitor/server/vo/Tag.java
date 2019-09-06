package com.eve.monitor.server.vo;

public class Tag {

    private String key;

    private String value;
    public Tag(){

    }

    public Tag(String key,String value){
        this.key = key;
        this.value =value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }


    public static Tag buildWithMarks(String name,String value){
        Tag tag = new Tag(name,"\""+value+"\"");
        return tag;
    }
}
