package com.bassitology_admin.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class StageModel implements Serializable {
    private String id;
    private String name;
    @Exclude
    private List<ClassModel> classes;

    public StageModel() {
    }

    public StageModel(String id, String name, List<ClassModel> classes) {
        this.id = id;
        this.name = name;
        this.classes = classes;
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

    @Exclude
    public List<ClassModel> getClasses() {
        return classes;
    }

    @Exclude
    public void setClasses(List<ClassModel> classes) {
        this.classes = classes;
    }
}
