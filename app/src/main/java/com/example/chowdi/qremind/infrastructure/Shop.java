package com.example.chowdi.qremind.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by L on 3/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Shop {
    private String address;
    private String category;
    private String email;
    private String phone_no;
    private long ratings;
    private String shop_name;
    private String vendor_id;
    private String image;

    public Shop() {
    }

    public Shop(String shop_name, String email) {
        this.shop_name = shop_name;
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public long getRatings() {
        return ratings;
    }

    public void setRatings(long ratings) {
        this.ratings = ratings;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
