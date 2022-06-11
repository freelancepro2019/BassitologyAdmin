package com.bassitology_admin.model;

import java.io.Serializable;

public class ChapterModel implements Serializable {
    private String id;
    private String user_id;
    private String name;

    public ChapterModel() {
    }

    public ChapterModel(String id, String user_id, String name) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
