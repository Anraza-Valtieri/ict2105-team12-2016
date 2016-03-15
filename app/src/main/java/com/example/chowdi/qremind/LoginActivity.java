package com.example.chowdi.qremind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
    private SharedPreferences prefs;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Check if there is already an authorisation for firebase which the user application have logged in previously
        fbRef = new Firebase(Constants.FIREBASE_MAIN);
        // if there is valid authorisation, redirect to next activity
        if(fbRef.getAuth() != null)
        {
            prefs = getSharedPreferences(Constants.SHARE_PREF_LINK, MODE_PRIVATE);
            String role = prefs.getString(Constants.SHAREPREF_ROLE, null);
            if(role.equals(Constants.ROLE_CUSTOMER))
                //nextActivityAfterLogin(CustomerProfilePageActivity.class);
                nextActivityAfterLogin(CustomerHomePageActivity.class);
            else if(role.equals(Constants.ROLE_VENDOR))
                nextActivityAfterLogin(ChooseTaskActivity.class);
        }

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Set listener to customer login button
        custLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginID = emailPhoneNoET.getText().toString();
                String password = passwordET.getText().toString();

                if(!checkMandatoryFields(loginID, password)) return;

                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }

                setEnableAllElements(false);
                customerLogin(loginID, password);
            }
        });
        vendorLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginID = emailPhoneNoET.getText().toString();
                String password = passwordET.getText().toString();

                if(!checkMandatoryFields(loginID, password)) return;

                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }

                setEnableAllElements(false);
                vendorLogin(loginID, password);
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
     * To check whether there are any errors on the mandatory fields
     * @param loginID login id
     * @param password password
     * @return true is passed | false is error.
     */
    private boolean checkMandatoryFields(String loginID, String password)
    {
        if (Commons.isEmptyString(loginID)) {
            emailPhoneNoET.setError("LoginID cannot be empty!");
            return false;
        }
        if (Commons.isEmptyString(password)) {
            passwordET.setError("Password cannot be empty!");
            return false;
        }
        if (!validateLoginID(loginID)) {
            emailPhoneNoET.setError("Please provide valid email or phone no!");
            Commons.showToastMessage("Please provide valid email or phone no!", getApplicationContext());
            return false;
        }

        // if no errors
        emailPhoneNoET.setError(null);
        passwordET.setError(null);
        return true;
    }

    /**
     * To login as customer
     * @param loginID login id
     * @param password password
     */
    private void vendorLogin(final String loginID, final String password)
    {
        Commons.showProgressDialog(pd, "Vendor login", "Logging in");
        fbRef = new Firebase(Constants.FIREBASE_VENDOR);
        final String email = loginID;
        if(Commons.isNumberString(loginID))
        {
            fbRef.child(loginID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null) {
                        emailPhoneNoET.setError("Phone No does not exists");
                        Commons.showToastMessage("Phone No does not exists", getApplicationContext());
                        Commons.dismissProgressDialog(pd);
                        setEnableAllElements(true);
                    }
                    else
                    {
                        fbRef.authWithPassword(dataSnapshot.child("email").getValue().toString(), password, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                String shopkey = dataSnapshot.child("shops").getChildren().iterator().next().getValue().toString();
                                saveAuthenticatedUserInfo(dataSnapshot.child("email").getValue().toString(), loginID, Constants.ROLE_VENDOR, shopkey);
                                Commons.dismissProgressDialog(pd);
                                nextActivityAfterLogin(ChooseTaskActivity.class);
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                handleFirebaseError(firebaseError);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    handleFirebaseError(firebaseError);
                }
            });
        }
        else if(Commons.isEmailString(loginID))
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
                                    String shopkey = ds.child("shops").getChildren().iterator().next().getValue().toString();
                                    saveAuthenticatedUserInfo(loginID, ds.child("phoneno").getValue().toString(), Constants.ROLE_VENDOR, shopkey);
                                    Commons.dismissProgressDialog(pd);
                                    nextActivityAfterLogin(ChooseTaskActivity.class);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            handleFirebaseError(firebaseError);
                        }
                    });
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    handleFirebaseError(firebaseError);
                }
            });
        }
    }

    /**
     * To login as customer
     * @param loginID login id
     * @param password password
     */
    private void customerLogin(final String loginID, final String password)
    {
        Commons.showProgressDialog(pd, "Customer login", "Logging in");
        fbRef = new Firebase(Constants.FIREBASE_CUSTOMER);
        final String email = loginID;
        if(Commons.isNumberString(loginID))
        {
            fbRef.child(loginID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null) {
                        emailPhoneNoET.setError("Phone No does not exists");
                        Commons.showToastMessage("Phone No does not exists", getApplicationContext());
                        Commons.dismissProgressDialog(pd);
                        setEnableAllElements(true);
                    }
                    else
                    {
                        fbRef.authWithPassword(dataSnapshot.child("email").getValue().toString(), password, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                saveAuthenticatedUserInfo(dataSnapshot.child("email").getValue().toString(), loginID, Constants.ROLE_CUSTOMER);
                                Commons.dismissProgressDialog(pd);
                                nextActivityAfterLogin(CustomerProfilePageActivity.class);
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                handleFirebaseError(firebaseError);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    handleFirebaseError(firebaseError);
                }
            });
        } else if (Commons.isEmailString(loginID))
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
                                    saveAuthenticatedUserInfo(loginID, ds.child("phoneno").getValue().toString(), Constants.ROLE_CUSTOMER);
                                    Commons.dismissProgressDialog(pd);
                                    nextActivityAfterLogin(CustomerProfilePageActivity.class);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            handleFirebaseError(firebaseError);
                        }
                    });
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    handleFirebaseError(firebaseError);
                }
            });
        }
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
            case FirebaseError.INVALID_PASSWORD:
                passwordET.setError("Password is invalid!");
                Commons.showToastMessage("Password is invalid!", getApplicationContext());
                break;
            case FirebaseError.USER_DOES_NOT_EXIST:
                emailPhoneNoET.setError("Email does not exist!");
                Commons.showToastMessage("Email does not exist!", getApplicationContext());
                break;
            default:
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                break;
        }
        setEnableAllElements(true);
        Commons.dismissProgressDialog(pd);
    }

    /**
     * Move to next activity after login
     * @param activityClass (some_activity).class
     */
    private void nextActivityAfterLogin(Class activityClass)
    {
        // if no valid authentication
        if(fbRef.getAuth() == null) return;

        Intent intent = new Intent(LoginActivity.this, activityClass);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    /**
     * This method will save the authenticated user's email and phoneNo to sharedpreferences
     * which will be used in other activity to retrieve information from Firebase.
     * @param email email address
     * @param phoneNo phone number
     */
    private void saveAuthenticatedUserInfo(String email, String phoneNo, String role, String... shopKey)
    {
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SHAREPREF_EMAIL, email);
        editor.putString(Constants.SHAREPREF_PHONE_NO, phoneNo);
        editor.putString(Constants.SHAREPREF_ROLE, role);
        if(role.equals(Constants.ROLE_VENDOR))
        {
            editor.putString(Constants.SHAREPREF_VENDOR_SHOP_KEY, shopKey[0]);
        }
        editor.commit();
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
        if(Commons.isEmailString(loginID))
            return true;
        else if(Commons.isNumberString(loginID))
            return true;
        return false;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // To cancel and dismiss all current toast
        Commons.cancelToastMessage();
    }
}

