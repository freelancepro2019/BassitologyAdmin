package com.bassitology_admin.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bassitology_admin.BR;

import java.io.Serializable;

public class AddLessonModel extends BaseObservable implements Serializable {
    private String chapter_id;
    private String lesson_name;
    private boolean valid;

    private void isDataValid(){
        if (!chapter_id.isEmpty()&&!lesson_name.trim().isEmpty()){
            setValid(true);
        }else {
            setValid(false);
        }
    }
    public AddLessonModel() {
        chapter_id ="";
        lesson_name="";
        valid =false;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
        isDataValid();
    }

    @Bindable
    public String getLesson_name() {
        return lesson_name;
    }

    public void setLesson_name(String lesson_name) {
        this.lesson_name = lesson_name;
        notifyPropertyChanged(BR.lesson_name);
        isDataValid();

    }

    @Bindable
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
        notifyPropertyChanged(BR.valid);
    }
}
