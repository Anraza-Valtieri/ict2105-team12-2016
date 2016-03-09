package com.example.chowdi.qremind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;

/**
 * Created by chowdi on 2/3/2016.
 */
public class ChooseTaskActivity extends AppCompatActivity{

    // Firebase variables
    Firebase fbRef;

    // Variables for all UI elements
    private Button manageBtn;
    private Button settingsBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosetask);

        // Initialise firebase
        fbRef = new Firebase(Constants.FIREBASE_MAIN);

        // Initialise all UI elements first
        initialiseUIElements();

        // set and implement click listener to manage, setting, and logout buttons
        manageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseTaskActivity.this, BusinessProfileActivity.class));
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.logout(fbRef, ChooseTaskActivity.this);
            }
        });
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        manageBtn = (Button) findViewById(R.id.choosetask_managequeuebtn);
        settingsBtn = (Button) findViewById(R.id.choosetask_settingsbtn);
        logoutBtn = (Button) findViewById(R.id.choosetask_logoutbtn);
    }
}
