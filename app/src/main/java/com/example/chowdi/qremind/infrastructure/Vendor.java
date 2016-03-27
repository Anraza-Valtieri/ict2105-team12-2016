package com.example.chowdi.qremind.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by L on 3/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Vendor {
    private String email;
    private String firstname;
    private String lastname;
    private String phoneno;

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
