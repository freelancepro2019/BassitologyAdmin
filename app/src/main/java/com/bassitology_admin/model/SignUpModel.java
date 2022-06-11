package com.bassitology_admin.model;

import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bassitology_admin.BR;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class SignUpModel extends BaseObservable implements Serializable {
    private String first_name;
    private String last_name;
    private String full_name;
    private String phone_code;
    private String phone;
    private String email;
    private String password;


    private StageModel stageModel;
    private ClassModel classModel;
    private SubjectModel subjectModel;
    @Exclude
    private boolean isValid;


    public SignUpModel() {
        first_name ="";
        last_name ="";
        full_name ="";
        phone_code="+20";
        phone ="";
        email ="";
        password ="";
        stageModel=null;
        classModel=null;
        subjectModel=null;
    }


    private void isDataValid(){
        if (!first_name.trim().isEmpty()&&
                !last_name.trim().isEmpty()&&
                !phone.trim().isEmpty()&&
                !email.trim().isEmpty()&&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()&&
                password.length()>5&&
                stageModel!=null&&
                classModel!=null&&
                subjectModel!=null
        ){
            full_name = first_name+" "+last_name;
            setValid(true);
        }else {
            setValid(false);
        }
    }

    @Bindable
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
        notifyPropertyChanged(BR.first_name);
        isDataValid();
    }

    @Bindable
    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
        notifyPropertyChanged(BR.last_name);
        isDataValid();
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }


    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
        isDataValid();
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
        notifyPropertyChanged(BR.email);
        isDataValid();
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
        notifyPropertyChanged(BR.password);
        isDataValid();
    }

    @Exclude
    @Bindable
    public boolean isValid() {
        return isValid;
    }

    @Exclude
    public void setValid(boolean valid) {
        isValid = valid;
        notifyPropertyChanged(BR.valid);
    }

    public StageModel getStageModel() {
        return stageModel;
    }

    public void setStageModel(StageModel stageModel) {
        this.stageModel = stageModel;

    }

    public ClassModel getClassModel() {
        return classModel;
    }

    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;

    }

    public SubjectModel getSubjectModel() {
        return subjectModel;
    }

    public void setSubjectModel(SubjectModel subjectModel) {
        this.subjectModel = subjectModel;

    }


}