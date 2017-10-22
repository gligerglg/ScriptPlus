package com.example.gayanlakshitha.scriptplus;

/**
 * Created by Gayan Lakshitha on 10/22/2017.
 */

public class Script {
    private String title;
    private String content;

    public Script() {

    }

    public Script(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
