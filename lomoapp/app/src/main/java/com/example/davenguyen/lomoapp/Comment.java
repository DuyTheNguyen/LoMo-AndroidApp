package com.example.davenguyen.lomoapp;

/**
 * Created by Dave Nguyen on 10-Oct-17.
 * This class is for storing Comment
 */


public class Comment {


    private String name;
    private String comment;
    private Long time;
    private String avatar;

    public Comment() {

    }

    public Comment(String name, String comment, Long time, String avatar) {
        this.name = name;
        this.comment = comment;
        this.time = time;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
