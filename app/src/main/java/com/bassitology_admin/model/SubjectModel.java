package com.bassitology_admin.model;

import java.io.Serializable;

public class SubjectModel implements Serializable {
    private String id;
    private String name;

    public SubjectModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SubjectModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
