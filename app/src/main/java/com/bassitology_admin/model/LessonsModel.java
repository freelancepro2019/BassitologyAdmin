package com.bassitology_admin.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class LessonsModel implements Serializable {
    private String id;
    private String chapter_id;
    private String user_id;
    private String name;
    private boolean canDelete = false;
    @Exclude
    private ChapterModel chapterModel;

    public LessonsModel() {
    }

    public LessonsModel(String id, String chapter_id, String user_id, String name) {
        this.id = id;
        this.chapter_id = chapter_id;
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

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    @Exclude
    public ChapterModel getChapterModel() {
        return chapterModel;
    }

    @Exclude
    public void setChapterModel(ChapterModel chapterModel) {
        this.chapterModel = chapterModel;
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
