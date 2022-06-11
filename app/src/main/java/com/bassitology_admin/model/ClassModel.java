package com.bassitology_admin.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class ClassModel implements Serializable {
    private String id;
    private String name;
    @Exclude
    private List<SubjectModel> subjects;

    public ClassModel() {
    }

    public ClassModel(String id, String name, List<SubjectModel> subjects) {
        this.id = id;
        this.name = name;
        this.subjects = subjects;
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
    public List<SubjectModel> getSubjects() {
        return subjects;
    }

    @Exclude
    public void setSubjects(List<SubjectModel> subjects) {
        this.subjects = subjects;
    }
}
