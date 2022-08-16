package com.byteteam.douyin.logic.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    private int uid;

    @ColumnInfo
    private String avatar;

    @ColumnInfo
    private String city;

    @ColumnInfo
    private String country;

    @ColumnInfo
    private String nickname;

    @ColumnInfo
    private String gender;

    private String introduction;

    private int subs;
    private int likes;
    private int fans;

    public User() {

    }

    public int getSubs() {
        return subs;
    }

    public void setSubs(int subs) {
        this.subs = subs;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public User(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @NonNull
    @Override
    public String toString() {
        return "Logged in user: {" +
                "uid: " +uid +
                ", \tnickname: " + nickname + "}";
    }

    public User getExampleUser(){
        this.setUid(12234);
        this.setAvatar("link for avatar");
        this.setCity("chongqing");
        this.setCountry("china");
        this.setNickname("panda panda");
        this.setGender("male");
        this.setIntroduction("hello , I am panda panda");
        return this;
    }
}