package com.example.admin.chat;

import android.net.Uri;

import java.util.Date;

/**
 * Created by admin on 12/1/2017.
 */

public class Model {

    private String isme;
    private String username;
    private String textmessage;
    private String user_id;
    private Long time;
    private Uri url;          //if i change this uri into string then error come on main activity of hashmap not convert into string

    public Model(String isme, String textmessage, String username, String user_id, Uri url) {
        this.isme = isme;
        this.username = username;
        this.textmessage = textmessage;
        this.user_id = user_id;
        this.url = url;
        time = new Date().getTime();
    }

    public Model(){
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getTextmessage() {
        return textmessage;
    }
    public void setTextmessage(String textmessage) {
        this.textmessage = textmessage;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public Uri getUrl() {
        return url;
    }
    public void setUrl(Uri url) {
        this.url = url;
    }
    public String getIsme() {
        return isme;
    }
    public void setIsme(String isme) {
        this.isme = isme;
    }
}
