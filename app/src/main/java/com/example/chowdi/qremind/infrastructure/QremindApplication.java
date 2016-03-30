package com.example.chowdi.qremind.infrastructure;

import android.app.Application;

import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
        //Initialize Firebase library in Android context before any FireBase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());
        //TODO: change avatarImage to avatarURL from user

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
