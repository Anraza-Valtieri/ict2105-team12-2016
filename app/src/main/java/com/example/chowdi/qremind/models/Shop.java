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
    private String shop_name;
    private long telephone;
    private long vendorid;
    private String image;

    public Shop() {
    }

    public Shop(String category, String email, String location, String shop_name, long telephone, long vendorid) {
        this.category = category;
        this.email = email;
        this.location = location;
        this.shop_name = shop_name;
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

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
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
