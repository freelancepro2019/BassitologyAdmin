package com.bassitology_admin.model;

import android.content.Context;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;


import com.bassitology_admin.BR;

import java.io.Serializable;

public class LoginModel extends BaseObservable implements Serializable {
    private String email;
    private String password;
    private boolean isValid;


    public LoginModel() {
        email ="";
        password ="";
    }

    private void isDataValid(){
        if (!email.trim().isEmpty()&&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()&&
                password.length()>0
        ){
            setValid(true);
        }else {
            setValid(false);
        }
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

    @Bindable
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
        notifyPropertyChanged(BR.valid);
    }
}