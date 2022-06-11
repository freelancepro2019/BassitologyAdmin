package com.bassitology_admin.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bassitology_admin.BR;

public class AddSocialModel extends BaseObservable {
    private String phone_code;
    private String whatsAppNumber;
    private String facebook;
    private String twitter;
    private String instagram;
    private String youtube;

    public AddSocialModel() {
        phone_code = "+20";
        whatsAppNumber ="";
        facebook ="";
        twitter ="";
        instagram ="";
        youtube ="";
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    @Bindable
    public String getWhatsAppNumber() {
        return whatsAppNumber;
    }

    public void setWhatsAppNumber(String whatsAppNumber) {
        if (whatsAppNumber.startsWith("0")){
            this.whatsAppNumber ="";
        }else {
            this.whatsAppNumber = whatsAppNumber;

        }
        notifyPropertyChanged(BR.whatsAppNumber);
    }

    @Bindable
    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
        notifyPropertyChanged(BR.facebook);
    }

    @Bindable
    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
        notifyPropertyChanged(BR.twitter);
    }

    @Bindable
    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
        notifyPropertyChanged(BR.instagram);
    }

    @Bindable
    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
        notifyPropertyChanged(BR.youtube);
    }
}
