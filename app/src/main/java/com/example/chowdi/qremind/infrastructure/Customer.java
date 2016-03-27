package com.example.chowdi.qremind.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    public Customer(){

    }

    public Customer(String email,String firstname,String lastname, String phoneno){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneno = phoneno;

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
