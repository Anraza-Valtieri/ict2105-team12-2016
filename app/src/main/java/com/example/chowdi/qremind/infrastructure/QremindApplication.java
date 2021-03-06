package com.example.chowdi.qremind.infrastructure;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import com.example.chowdi.qremind.Customer.CurrentServingActivity;
import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Contributed by Chin Zhi Qiang, Anton Salim on 3/26/2016.
 * Used as a singleton point
 * No constructor because may not be initialized yet by then.
 * onCreate would be a better place
 */
public class QremindApplication extends Application {

    private Customer customerUser;
    private Vendor vendorUser;
    public Boolean notificationSend;

    private Firebase accFbRef;
    private ValueEventListener accListener;

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize Firebase library in Android context before any FireBase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());
        notificationSend = false;
        customerUser = null;
        vendorUser = null;
        accFbRef = null;
        accListener = null;
    }

    @Override
    public void onTerminate()
    {
        if(accFbRef != null && accListener != null)
        {
            accFbRef.removeEventListener(accListener);
        }
        super.onTerminate();
    }

    public Customer getCustomerUser() {
        return (customerUser==null)?null:customerUser;
    }

    /* To be called by logout */
    public void releaseCustomerUser()
    {
        this.customerUser = null;
    }

    /* To be called by logout */
    public void releaseVendorUser()
    {
        this.vendorUser = null;
    }

    public void setCustomerUser(Customer customerUser) {
        if(this.customerUser == null) {
            this.customerUser = customerUser;
            startAccountListener(Constants.ROLE_CUSTOMER);
        }
    }

    public Vendor getVendorUser() {
        return (vendorUser==null)?null:vendorUser;
    }

    public void setVendorUser(Vendor vendorUser) {
        if(this.vendorUser == null) {
            this.vendorUser = vendorUser;
            startAccountListener(Constants.ROLE_VENDOR);
        }
    }

    public void showNotification(Activity context) {
        notificationSend = true;
        Intent intent = new Intent(context, CurrentServingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.notification_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(r.getString(R.string.notification_title))
                .setContentText(r.getString(R.string.notification_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private void startAccountListener(String role)
    {
        if(accFbRef != null && accListener != null)
        {
            accFbRef.removeEventListener(accListener);
        }
        if(role.equals(Constants.ROLE_CUSTOMER))
            accFbRef = new Firebase(Constants.FIREBASE_CUSTOMER+"/"+customerUser.getPhoneno());
        else
            accFbRef = new Firebase(Constants.FIREBASE_VENDOR+"/"+vendorUser.getPhoneno());

        accListener = accFbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(customerUser != null) {
                    customerUser = dataSnapshot.getValue(Customer.class);
                }
                else
                    vendorUser = dataSnapshot.getValue(Vendor.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
            }
        });
    }
}
