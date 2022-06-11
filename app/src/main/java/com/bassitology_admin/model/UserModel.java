package com.bassitology_admin.model;


public class UserModel{
    private String id;
    private String first_name;
    private String last_name;
    private String full_name;
    private String phone_code;
    private String phone;
    private String email;
    private String password;
    private String whatsapp="";
    private String facebook="";
    private String twitter="";
    private String instagram="";
    private String youtube="";
    private StageModel stageModel;
    private ClassModel classModel;
    private SubjectModel subjectModel;


    public UserModel() {
    }

    public UserModel(String id, String first_name, String last_name, String full_name, String phone_code, String phone, String email, String password, StageModel stageModel, ClassModel classModel, SubjectModel subjectModel) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.full_name = full_name;
        this.phone_code = phone_code;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.stageModel = stageModel;
        this.classModel = classModel;
        this.subjectModel = subjectModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
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
