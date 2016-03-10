package com.example.chowdi.qremind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by chowdi on 2/3/2016.
 */
public class CustomerProfilePageActivity extends AppCompatActivity{

    // Firebase variables
    Firebase fbRef;

    // Variables for all UI elements
    private EditText fName_ET;
    private EditText lName_ET;
    private EditText email_ET;
    private EditText phoneNo_ET;
    private Button updateBtn;
    private Button logoutBtn;

    // Other variables
    private SharedPreferences prefs;
    private String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerprofilepage);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise all UI elements first
        initialiseUIElements();

        // Initialise getSharedPreferences for this app and Firebase setup
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);
        fbRef = new Firebase(Constants.FIREBASE_CUSTOMER);

        // Retrieve phone no from share preference to retrieve user information and display on the view
        phoneNo = prefs.getString(Constants.SHAREPREF_PHONE_NO, null);
        // Check network connection
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            setEnableAllElements(false);
        }else {
            fbRef.child(phoneNo).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fName_ET.setText(dataSnapshot.child("firstname").getValue().toString());
                    lName_ET.setText(dataSnapshot.child("lastname").getValue().toString());
                    email_ET.setText(dataSnapshot.child("email").getValue().toString());
                    phoneNo_ET.setText(dataSnapshot.child("phoneno").getValue().toString());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    handleFirebaseError(firebaseError);
                }
            });
        }

        // Set and implement listener to update button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }
                setEnableAllElements(false);

                // If session expired, close current activity and go to login activity
                if(fbRef.getAuth() == null)
                {
                    Commons.logout(fbRef, CustomerProfilePageActivity.this);
                    Commons.showToastMessage("Your session expired!", getApplicationContext());
                    return;
                }

                fbRef.child(phoneNo).child("firstname").setValue(fName_ET.getText().toString());
                fbRef.child(phoneNo).child("lastname").setValue(lName_ET.getText().toString());
                Commons.showToastMessage("Profile updated!", getApplicationContext());
                setEnableAllElements(true);
            }
        });

        // For temporary only
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.logout(fbRef, CustomerProfilePageActivity.this);
            }
        });
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        fName_ET = (EditText) findViewById(R.id.custProfile_fName_ET);
        lName_ET = (EditText) findViewById(R.id.custProfile_lName_ET);
        email_ET = (EditText) findViewById(R.id.custProfile_email_ET);
        phoneNo_ET = (EditText) findViewById(R.id.custProfile_phone_no_ET);
        updateBtn = (Button) findViewById(R.id.custProfile_udpatebtn);
        logoutBtn = (Button) findViewById(R.id.custProfile_logoutbtn);
    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        fName_ET.setEnabled(value);
        lName_ET.setEnabled(value);
        email_ET.setEnabled(value);
        phoneNo_ET.setEnabled(value);
        updateBtn.setEnabled(value);
        logoutBtn.setEnabled(value);
    }

    /**
     * To handle all kind of firebase errors where to show the appropriate
     * and correct error messages on each errors
     * @param firebaseError FirebaseError
     */
    private void handleFirebaseError(FirebaseError firebaseError)
    {
        switch (firebaseError.getCode())
        {
            default:
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                break;
        }
        setEnableAllElements(true);
    }
}
