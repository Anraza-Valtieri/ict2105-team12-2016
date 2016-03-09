package com.example.chowdi.qremind.models;

/**
 * Created by L on 3/5/2016.
 */
public class Shop {
    private String category;
    private String email;
    private String location;
    private String shopname;
    private String telephone;
    private String vendorid;

    public Shop(String category, String email, String location, String shopname, String telephone, String vendorid) {
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVendorid() {
        return vendorid;
    }

    public void setVendorid(String vendorid) {
        this.vendorid = vendorid;
    }
}
