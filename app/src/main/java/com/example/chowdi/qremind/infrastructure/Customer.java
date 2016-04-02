package com.example.chowdi.qremind.infrastructure;

import android.graphics.Bitmap;

import com.example.chowdi.qremind.utils.Commons;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by L on 3/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Customer{

    private String email;
    private String firstname;
    private String lastname;
    private String phoneno;
    private Map<String,Object> current_queue;
    private String image;
    private Bitmap myImage;

    public Customer(){

    }

    public Customer(String email,String firstname,String lastname, String phoneno){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneno = phoneno;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getImage() {
        return image;
    }

    @JsonIgnore
    public Bitmap getMyImage() {
        return myImage;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @JsonIgnore
    public void setMyImage(Bitmap myImage) {
        if(myImage == null)
            setMyImage(Commons.convertBase64ToBitmap(image));
        this.myImage = myImage;
    }

    public Map<String,Object> getCurrent_queue() {
        return current_queue;
    }

    public void setCurrent_queue(Map<String, Object> current_queue) {
        this.current_queue = current_queue;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhoneno() {
        return phoneno;
    }
}
