package com.sanmathi.cryptos.Models;

public class User {

    private String id;
    private String fullname;
    private String imageurl;
    private String bio;

    public User() {
    }

    public User(String id,  String fullname, String imageurl, String bio) {
        this.id = id;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
