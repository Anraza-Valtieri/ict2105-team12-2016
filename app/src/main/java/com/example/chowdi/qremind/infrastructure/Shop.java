package com.example.chowdi.qremind.infrastructure;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.chowdi.qremind.utils.Commons;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by L on 3/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Shop implements Parcelable {
    private String address;
    private String category;
    private String email;
    private String phone_no;
    private long ratings;
    private String shop_name;
    private String vendor_id;
    private String image;
    private Bitmap myImage;
    private long queueCount;
    private String shop_key;
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
        setMyImage(Commons.convertBase64ToBitmap(image));
    }

    public String getImage() {
        return image;
    }

    public long getQueueCount() {
        return queueCount;
    }

    public void setQueueCount(long queueCount) {
        this.queueCount = queueCount;
    }

    public String getShop_key() {
        return shop_key;
    }

    public void setShop_key(String shop_key) {
        this.shop_key = shop_key;
    }

    @JsonIgnore
    public Bitmap getMyImage() {
        return myImage;
    }

    @JsonIgnore
    public void setMyImage(Bitmap myImage) {
        this.myImage = myImage;
    }

    protected Shop(Parcel in) {
        address = in.readString();
        category = in.readString();
        email = in.readString();
        phone_no = in.readString();
        ratings = in.readLong();
        shop_name = in.readString();
        vendor_id = in.readString();
        image = in.readString();
        queueCount = in.readLong();
        shop_key = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(category);
        dest.writeString(email);
        dest.writeString(phone_no);
        dest.writeLong(ratings);
        dest.writeString(shop_name);
        dest.writeString(vendor_id);
        dest.writeString(image);
        dest.writeLong(queueCount);
        dest.writeString(shop_key);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
}