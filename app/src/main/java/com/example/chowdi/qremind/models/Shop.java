package com.example.chowdi.qremind.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by L on 3/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Shop {
    private String category;
    private String email;
    private String location;
    private String shopname;
    private long telephone;
    private long vendorid;
    private String image;

    public Shop() {
    }

    public Shop(String category, String email, String location, String shopname, long telephone, long vendorid) {
        this.category = category;
        this.email = email;
        this.location = location;
        this.shopname = shopname;
        this.telephone = telephone;
        this.vendorid = vendorid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public long getTelephone() {
        return telephone;
    }

    public void setTelephone(long telephone) {
        this.telephone = telephone;
    }

    public long getVendorid() {
        return vendorid;
    }

    public void setVendorid(long vendorid) {
        this.vendorid = vendorid;
    }

    public String getImage() {
        return image;
    }
}
