package com.example.chowdi.qremind;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.utils.CustomisedTextWatcher;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Register_Activity extends BaseActivity{

    // Variable for Firebase
    private Firebase fbRef;

    // Variable for all relevant UI elements
    private EditText fNameET;
    private EditText lNameET;
    private EditText emailET;
    private EditText phoneNoET;
    private EditText pwdET;
    private EditText cPwdET;
    private RadioGroup roleRG;
    private Button registerBtn;

    // Others variables
    private boolean anyETErrors = false;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Set listener to register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentFocus().clearFocus(); // Clear all focuses in the view

                // Check network connection
                if (!Commons.isNetworkAvailable(getApplicationContext())) {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }

                Boolean editTextErr = isEmptyField(fNameET) | isEmptyField(lNameET) | isEmptyField(emailET) |
                        isEmptyField(phoneNoET) | isEmptyField(pwdET) | isEmptyField(cPwdET);

                // To hide the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (editTextErr) return;
                if (anyETErrors) return;

                setEnableAllElements(false);
                registerUser();
            }
        });
        // Set on focus changed listener to password EditText
        pwdET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pwd = pwdET.getText().toString();
                    String cPwd = cPwdET.getText().toString();
                    if(!isEmptyField(pwdET))
                    {
                        if(!pwd.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"))
                        {
                            pwdET.setError("Must contains minimum 8 characters at least 1 uppercase, 1 lowercase and 1 Number.");
                            anyETErrors = true;
                        }
                        return;
                    }
                    else
                    {
                        pwdET.setError(null);
                    }
                    if (pwd.equals(cPwd)) {
                        anyETErrors = false;
                        cPwdET.setError(null);
                    }

                    if(pwdET.getText().toString().isEmpty())
                    {
                        ((TextView)findViewById(R.id.password_textView)).setVisibility(View.INVISIBLE);
                        pwdET.setHint(R.string.hint_password);
                    }
                    else
                    {
                        ((TextView)findViewById(R.id.password_textView)).setVisibility(View.VISIBLE);
                    }
                }
                else {
                    ((TextView)findViewById(R.id.password_textView)).setVisibility(View.VISIBLE);
                    pwdET.setHint("");
                }
            }
        });
        // Set on focus changed listener to confirmed password EditText
        cPwdET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    String pwd = pwdET.getText().toString();
                    String cPwd = cPwdET.getText().toString();
                    if(!pwd.equals(cPwd))
                    {
                        cPwdET.setError("Confirm password must match with Password!");
                        anyETErrors = true;
                    }
                    else
                    {
                        anyETErrors = false;
                        cPwdET.setError(null);
                    }

                    if(cPwdET.getText().toString().isEmpty())
                    {
                        ((TextView)findViewById(R.id.confirmpassword_textView)).setVisibility(View.INVISIBLE);
                        cPwdET.setHint(R.string.hint_confirmpassword);
                    }
                    else
                    {
                        ((TextView)findViewById(R.id.confirmpassword_textView)).setVisibility(View.VISIBLE);
                    }
                }
                else {
                    ((TextView)findViewById(R.id.confirmpassword_textView)).setVisibility(View.VISIBLE);
                    cPwdET.setHint("");
                }
            }
        });
        // Set on focus changed listener to email EditText
        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (emailET.getText().toString().isEmpty()) {
                        ((TextView) findViewById(R.id.email_textView)).setVisibility(View.INVISIBLE);
                        emailET.setHint(R.string.hint_email);
                    } else {
                        ((TextView) findViewById(R.id.email_textView)).setVisibility(View.VISIBLE);
                    }

                    if (isEmptyField(emailET)) {
                        emailET.setError(null);
                        return;
                    }
                    String email = emailET.getText().toString();
                    if (!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                        emailET.setError("Please provide valid email!");
                        anyETErrors = true;
                    } else {
                        emailET.setError(null);
                        anyETErrors = false;
                    }
                } else {
                    ((TextView) findViewById(R.id.email_textView)).setVisibility(View.VISIBLE);
                    emailET.setHint("");
                }
            }
        });

        fNameET.addTextChangedListener(new CustomisedTextWatcher(fNameET, (TextView) findViewById(R.id.fname_textView), R.string.hint_fname));
        lNameET.addTextChangedListener(new CustomisedTextWatcher(lNameET, (TextView) findViewById(R.id.lname_textView), R.string.hint_fname));
        phoneNoET.addTextChangedListener(new CustomisedTextWatcher(phoneNoET, (TextView) findViewById(R.id.phonenumber_textView), R.string.hint_phone_no));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        fNameET = (EditText) findViewById(R.id.FirstName_editText);
        lNameET = (EditText) findViewById(R.id.LastName_editText);
        emailET = (EditText) findViewById(R.id.Email_editText);
        phoneNoET = (EditText) findViewById(R.id.Phone_editText);
        pwdET = (EditText) findViewById(R.id.Password_editText);
        cPwdET = (EditText) findViewById(R.id.ConfirmPassword_editText);
        roleRG = (RadioGroup) findViewById(R.id.Role_RadioGroup);
        registerBtn = (Button) findViewById(R.id.Register_button);
    }

    /**
     * Check if str is empty
     * @return true if empty, else false for non-empty
     */
    private Boolean isEmptyField(EditText editText)
    {
        if(TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("This field is mandatory!");
            return true;
        }
        editText.setError(null);
        return false;
    }

    /**
     * Get selected role from radio group
     * @return selected role
     */
    private String getFBLinkForSelectedRole()
    {
        switch (roleRG.getCheckedRadioButtonId())
        {
            case R.id.customer_radiobtn:
                return Constants.FIREBASE_CUSTOMER;
            case R.id.vendor_radiobtn:
                return Constants.FIREBASE_VENDOR;
            default:
                return null;
        }
    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        fNameET.setEnabled(value);
        lNameET.setEnabled(value);
        emailET.setEnabled(value);
        phoneNoET.setEnabled(value);
        pwdET.setEnabled(value);
        cPwdET.setEnabled(value);
        roleRG.setEnabled(value);
        registerBtn.setEnabled(value);
    }

    /**
     * Create a new user account in Firebase
     */
    private void registerUser()
    {
        Commons.showProgressDialog(pd, "Registration", "Creating account");

        final String fname = fNameET.getText().toString();
        final String lname = lNameET.getText().toString();
        final String email = emailET.getText().toString();
        final String phoneNo = phoneNoET.getText().toString();
        final String pwd = pwdET.getText().toString();

        fbRef = new Firebase(getFBLinkForSelectedRole());

        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(phoneNo)) {
                    fbRef.createUser(email, pwd, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("provider", "password");
                            map.put("firstname", fname);
                            map.put("lastname", lname);
                            map.put("phoneno", phoneNo);
                            map.put("email", email);
                            map.put("uid", result.get("uid").toString());

                            fbRef.child(phoneNoET.getText().toString()).setValue(map);
                            Commons.showToastMessage("Registration Successful", getApplicationContext());
                            Register_Activity.this.finish();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            handleFirebaseError(firebaseError);
                        }
                    });
                } else {
                    Commons.showToastMessage("Phone already exists", getApplicationContext());
                    phoneNoET.setError("Phone already exists");
                    // Enable all the UI elements after this process done and dismiss the progress dialog
                    setEnableAllElements(true);
                    Commons.dismissProgressDialog(pd);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
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
            case FirebaseError.EMAIL_TAKEN:
                Commons.showToastMessage("Email is already taken", getApplicationContext());
                emailET.setError("Email is already taken.");
                break;
            default:
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                break;
        }
        setEnableAllElements(true);
        Commons.dismissProgressDialog(pd);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // To cancel and dismiss all current toast
        Commons.cancelToastMessage();
    }
}
