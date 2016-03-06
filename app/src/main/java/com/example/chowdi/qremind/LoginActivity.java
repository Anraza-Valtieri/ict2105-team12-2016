package com.example.chowdi.qremind;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // Variable for Firebase
    private Firebase fbRef;

    // Variable for all relevant UI elements
    private EditText emailPhoneNoET;
    private EditText passwordET;
    private Button custLoginBtn;
    private Button vendorLoginBtn;

    // Others variables
    private Boolean dataRetrieved = false;
    private Boolean phoneNoExist = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Check if there is already an authorisation for firebase which the user application have logged in previously
        fbRef = new Firebase(getString(R.string.fb_main_link));
        // if there is valid authorisation, redirect to next activity
        if(fbRef.getAuth() != null)
        {
            Intent intent = new Intent(LoginActivity.this, CustomerProfilePageActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }

        // Initialise all UI elements first
        initialiseUIElements();

        // Set listener to customer login button
        custLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginID = emailPhoneNoET.getText().toString();
                String password = passwordET.getText().toString();

                if (isEmptyString(loginID)) {
                    emailPhoneNoET.setError("LoginID cannot be empty!");
                    return;
                }
                if (isEmptyString(password)) {
                    passwordET.setError("Password cannot be empty!");
                    return;
                }
                if (!validateLoginID(loginID)) {
                    emailPhoneNoET.setError("Please provide valid email or phone no!");
                    showToastMessage("Please provide valid email or phone no!");
                    return;
                }
                emailPhoneNoET.setError(null);
                passwordET.setError(null);

                setEnableAllElements(false);
                customerLogin(loginID, password);
            }
        });
        // add and implement text changed listener to email edit text
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordET.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // add and implement text changed listener to email edit text
        emailPhoneNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailPhoneNoET.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        emailPhoneNoET = (EditText) findViewById(R.id.email_login_ET);
        passwordET = (EditText) findViewById(R.id.password_login_ET);
        custLoginBtn = (Button) findViewById(R.id.cust_login_btn);
        vendorLoginBtn = (Button) findViewById(R.id.vendor_login_btn);
    }

    /**
     * To login as customer
     * @param loginID login id
     * @param password password
     */
    private void customerLogin(final String loginID, final String password)
    {
        fbRef = new Firebase(getString(R.string.fb_appuseracc_cust));
        final String email = loginID;
        if(isNumberString(loginID))
        {
            fbRef.child(loginID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null) {
                        emailPhoneNoET.setError("Phone No does not exists");
                        showToastMessage("Phone No does not exists");
                        setEnableAllElements(true);
                    }
                    else
                    {
                        fbRef.authWithPassword(dataSnapshot.child("email").getValue().toString(), password, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                prefs = getSharedPreferences(getString(R.string.shared_pref_main),MODE_PRIVATE);
                                final SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("email", dataSnapshot.child("email").getValue().toString());
                                editor.putString("phoneNo", loginID);
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, CustomerProfilePageActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                switch (firebaseError.getCode())
                                {
                                    case FirebaseError.INVALID_PASSWORD:
                                        passwordET.setError("Password is invalid!");
                                        showToastMessage("Password is invalid!");
                                        break;
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
        else if(isEmailString(loginID))
        {
            fbRef.authWithPassword(loginID, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                if(ds.child("email").getValue().toString().equals(loginID))
                                {
                                    prefs = getSharedPreferences(getString(R.string.shared_pref_main),MODE_PRIVATE);
                                    final SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email", loginID);
                                    editor.putString("phoneNo", ds.child("phoneno").getValue().toString());
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity.this, CustomerProfilePageActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    switch (firebaseError.getCode())
                    {
                        case FirebaseError.INVALID_PASSWORD:
                            passwordET.setError("Password is invalid!");
                            showToastMessage("Password is invalid!");
                            break;
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            emailPhoneNoET.setError("Email does not exist!");
                            showToastMessage("Email does not exist!");
                            break;
                    }
                    setEnableAllElements(true);
                }
            });
        }
    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        emailPhoneNoET.setEnabled(value);
        passwordET.setEnabled(value);
        custLoginBtn.setEnabled(value);
        vendorLoginBtn.setEnabled(value);
    }


    /**
     * Validate the login ID whether it is an valid email or phone number
     * @return true if valid else false for invalid
     */
    private Boolean validateLoginID(String loginID)
    {
        if(isEmailString(loginID))
            return true;
        else if(isNumberString(loginID))
            return true;
        return false;
    }

    /**
     * Check if the value is an valid email
     * @param value email string
     * @return true for valid, false for invalid
     */
    private Boolean isEmailString(String value) {
        String email_reg_exp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return value.matches(email_reg_exp);
    }

    /**
     * Check if the value is a number
     * @param value number string
     * @return true for valid, false for invalid
     */
    private Boolean isNumberString(String value) {
        String number_reg_exp = "^[0-9]*$";
        return value.matches(number_reg_exp);
    }

    /**
     * Check if str is empty
     * @return true if empty, else false for non-empty
     */
    private Boolean isEmptyString(String value) {
        return TextUtils.isEmpty(value);
    }

    /**
     * Show any messages on Toast
     * @param message - message string
     */
    private void showToastMessage(String message)
    {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}

