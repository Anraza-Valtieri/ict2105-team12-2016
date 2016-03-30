package com.example.chowdi.qremind.infrastructure;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by L on 3/26/2016.
 * Used as a singleton point
 * No constructor because may not be initialized yet by then.
 * onCreate would be a better place
 */
public class QremindApplication extends Application {

    private Customer customerUser;
    private Vendor vendorUser;
    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize Firebase library in Android context before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());
    }

    public Customer getCustomerUser() {
        return customerUser;
    }

    public void setCustomerUser(Customer customerUser) {
        this.customerUser = customerUser;
    }

    public Vendor getVendorUser() {
        return vendorUser;
    }

    public void setVendorUser(Vendor vendorUser) {
        this.vendorUser = vendorUser;
    }
}
