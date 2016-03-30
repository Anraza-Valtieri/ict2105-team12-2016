package com.example.chowdi.qremind.infrastructure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by L on 3/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Customer implements CardWithList.ListObject{

    private String email;
    private String firstname;
    private String lastname;
    private String phoneno;
    private Map<String,String> current_queue;
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

    public Bitmap getMyImage() {
        return myImage;
    }

    public void setImage(String image) {
        this.image = image;
        convertImage(image);
    }

    public void setMyImage(Bitmap myImage) {
        this.myImage = myImage;
    }

    public Map<String,String> getCurrent_queue() {
        return current_queue;
    }

    public void setCurrent_queue(Map<String, String> current_queue) {
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

    public void convertImage(String imageString)
    {
        byte[] imageAsBytes = Base64.decode(imageString.getBytes(), Base64.DEFAULT);
        setMyImage(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
    }
    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public Card getParentCard() {
        return null;
    }

    @Override
    public void setOnItemClickListener(CardWithList.OnItemClickListener onItemClickListener) {

    }

    @Override
    public CardWithList.OnItemClickListener getOnItemClickListener() {
        return null;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public void setSwipeable(boolean isSwipeable) {

    }

    @Override
    public CardWithList.OnItemSwipeListener getOnItemSwipeListener() {
        return null;
    }

    @Override
    public void setOnItemSwipeListener(CardWithList.OnItemSwipeListener onSwipeListener) {

    }
}
