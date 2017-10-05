package com.example.gayanlakshitha.scriptplus;

/**
 * Created by Gayan Lakshitha on 9/26/2017.
 */

public class Note {
    private String topic;
    private String content;
    private boolean isProtected = false;
    private String password = null;

    public Note(String topic, String content, boolean isProtected, String password) {
        this.topic = topic;
        this.content = content;
        this.isProtected = isProtected;
        this.password = password;
    }

    public Note(String topic, String content, boolean isProtected) {
        this.topic = topic;
        this.content = content;
        this.isProtected = isProtected;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
