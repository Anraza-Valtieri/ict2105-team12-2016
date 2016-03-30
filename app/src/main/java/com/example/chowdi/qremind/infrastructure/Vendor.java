package com.example.chowdi.qremind.infrastructure;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created by L on 3/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Vendor {
    private String email;
    private String firstname;
    private String lastname;
    private String phoneno;
    private String image;
    private Bitmap myImage;
    private Map<String, Object> shops;

    public Vendor(){

    }

    public Vendor(String email,String firstname,String lastname, String phoneno){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneno = phoneno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getMyImage() {
        return myImage;
    }

    public void setMyImage(Bitmap myImage) {
        this.myImage = myImage;
    }

    public Map<String, Object> getShops() {
        return shops;
    }

    public void setShops(Map<String, Object> shops) {
        this.shops = shops;
    }
}
