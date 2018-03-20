package com.plplim.david.calendar.model;

/**
 * Created by OHRok on 2018-02-27.
 */

public class Todo {
    public String num;
    public String id;
    public String title;
    public String content;
    public String date;
    public String time;
    public String share;

    public Todo(String num,String id, String title, String content, String date, String time, String share) {
        this.num = num;
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.share = share;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }
}
