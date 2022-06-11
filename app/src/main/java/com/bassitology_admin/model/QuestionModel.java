package com.bassitology_admin.model;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bassitology_admin.BR;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionModel extends BaseObservable implements Serializable {
    private String question_id;
    private String lesson_id;
    private String user_id;
    private String exam_id;
    private String chapter_id;
    private String image="";
    private String title="";
    private String link="";
    @Exclude
    private String uri;
    private List<AnswerModel> answers;
    @Exclude
    private boolean isValid;


    public QuestionModel() {
        answers = new ArrayList<>();
        title = "";
        uri = "";
        link = "";
    }

    public QuestionModel(String question_id, String lesson_id, String user_id, String exam_id, String chapter_id, String image, String link, List<AnswerModel> answers) {
        this.question_id = question_id;
        this.lesson_id = lesson_id;
        this.user_id = user_id;
        this.image = image;
        this.answers = answers;
        this.exam_id = exam_id;
        this.chapter_id = chapter_id;
        this.link = link;
    }

    @Exclude
    private void isDataValid() {
        Log.e("uri",uri+"__"+answers.size());
        if (!uri.isEmpty() &&
                answers.size() >= 4
        ) {
            setValid(true);
        } else {
            setValid(false);
        }
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude
    public String getUri() {
        return uri;
    }

    @Exclude
    public void setUri(String uri) {
        this.uri = uri;
        isDataValid();

    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title!=null?title:"";
        notifyPropertyChanged(BR.title);
        isDataValid();
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }

    @Exclude
    public void addAnswer(AnswerModel model) {
        answers.add(model);
        isDataValid();
    }

    @Exclude
    public void removeAnswer(int pos) {
        answers.remove(pos);
        isDataValid();

    }

    public void updateAnswer(AnswerModel model, int pos) {
        answers.set(pos, model);
    }

    @Bindable
    @Exclude
    public boolean isValid() {
        return isValid;
    }

    @Exclude
    public void setValid(boolean valid) {
        isValid = valid;
        notifyPropertyChanged(BR.valid);
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getExam_id() {
        return exam_id;
    }

    public void setExam_id(String exam_id) {
        this.exam_id = exam_id;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    @Bindable
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link!=null?link:"";
        notifyPropertyChanged(BR.link);
    }
}
