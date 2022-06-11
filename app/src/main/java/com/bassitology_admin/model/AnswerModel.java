package com.bassitology_admin.model;

import java.io.Serializable;

public class AnswerModel implements Serializable {
    private String question_id;
    private String title;
    private boolean answer;

    public AnswerModel() {
    }

    public AnswerModel(String title, boolean answer) {
        this.title = title;
        this.answer = answer;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}
