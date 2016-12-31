package com.example.ahmaadyunus.eventattendance.model;

/**
 * Created by ahmaadyunus on 30/12/16.
 */

public class Participant {


    private String id;
    private String no_ktp;
    private String name;
    private String mobile;
    private String address;
    private String email;
    private String profile_pict;

    public Participant () {

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getNo_ktp() {
        return no_ktp;
    }

    public void setNo_ktp(String no_ktp) {
        this.no_ktp = no_ktp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_pict() {
        return profile_pict;
    }

    public void setProfile_pict(String profile_pict) {
        this.profile_pict = profile_pict;
    }



}
