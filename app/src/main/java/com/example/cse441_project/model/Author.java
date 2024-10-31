package com.example.cse441_project.model;

public class Author {
    private String name;
    private String gender;
    private String avatarUrl;

    public Author() {}

    public Author(String name, String gender, String avatarUrl) {
        this.name = name;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
