package com.bassitology_admin.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class ExamModel implements Serializable {
    private String id;
    private String user_id;
    private String subject_id; //maybe null
    private String chapter_id;
    private String lesson_id;
    private String title;
    @Exclude
    private boolean canDelete = false;

    public ExamModel() {
    }

    public ExamModel(String id, String user_id, String subject_id, String chapter_id, String lesson_id, String title) {
        this.id = id;
        this.user_id = user_id;
        this.subject_id = subject_id;
        this.chapter_id = chapter_id;
        this.lesson_id = lesson_id;
        this.title = title;
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

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public boolean isCanDelete() {
        return canDelete;
    }

    @Exclude
    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }
}
