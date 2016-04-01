package com.example.chowdi.qremind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chowdi.qremind.Customer.CustomerHomePageActivity;
import com.example.chowdi.qremind.Vendor.VendorDashBoardActivity;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Customer;
import com.example.chowdi.qremind.infrastructure.Vendor;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LogoCoverPageActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Check if there is already an authorisation for firebase which the user application have logged in previously
        Firebase fbRef = new Firebase(Constants.FIREBASE_MAIN);
        SharedPreferences prefs;
        // if there is valid authorisation, redirect to next activity
        if(fbRef.getAuth() != null)
        {
            prefs = getSharedPreferences(Constants.SHARE_PREF_LINK, MODE_PRIVATE);
            String role = prefs.getString(Constants.SHAREPREF_ROLE, null);
            String phoneNo = prefs.getString(Constants.SHAREPREF_PHONE_NO, null);
            if(phoneNo == null || role == null)
            {
                fbRef.unauth();
                nextActivityAfterLogin(Login_RegisterActivity.class);
            }
            else
                retrieveAccountInfo(phoneNo, role);
        }
        else {
            nextActivityAfterLogin(Login_RegisterActivity.class);
        }
    }

    private void retrieveAccountInfo(String phoneNo, final String role)
    {
        Firebase accFbRef;
        if(role.equals(Constants.ROLE_CUSTOMER))
            accFbRef= new Firebase(Constants.FIREBASE_CUSTOMER+"/"+phoneNo);
        else
            accFbRef= new Firebase(Constants.FIREBASE_VENDOR+"/"+phoneNo);
        accFbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(role.equals(Constants.ROLE_CUSTOMER))
                {
                    Customer custUser = dataSnapshot.getValue(Customer.class);
                    getQremindApplication().setCustomerUser(custUser);
                    nextActivityAfterLogin(CustomerHomePageActivity.class);
                }
                else if(role.equals(Constants.ROLE_VENDOR))
                {
                    Vendor vendorUser = dataSnapshot.getValue(Vendor.class);
                    getQremindApplication().setVendorUser(vendorUser);
                    nextActivityAfterLogin(VendorDashBoardActivity.class);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
            }
        });
    }

    /**
     * Move to next activity after login
     * @param activityClass (some_activity).class
     */
    private void nextActivityAfterLogin(Class activityClass)
    {
        Intent intent = new Intent(LogoCoverPageActivity.this, activityClass);
        startActivity(intent);
        LogoCoverPageActivity.this.finish();
    }
}
