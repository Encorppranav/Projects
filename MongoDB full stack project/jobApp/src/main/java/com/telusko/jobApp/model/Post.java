package com.telusko.jobApp.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;

@Document(collection = "PostingInfo")
public class Post

{
    private String profile;
    private String desc;
    private int exp;
    private String techs[];



    @Override
    public String toString() {
        return "Post{" +

                ", desc='" + desc + '\'' +
                ", exp=" + exp +
                ", profile='" + profile + '\'' +
                ", techs=" + Arrays.toString(techs) +
                '}';
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setTechs(String[] techs) {
        this.techs = techs;
    }



    public Post() {
    }

    public String getDesc() {
        return desc;
    }

    public int getExp() {
        return exp;
    }

    public String getProfile() {
        return profile;
    }

    public String[] getTechs() {
        return techs;
    }



}
