package com.example.chowdi.qremind;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chowdi on 2/3/2016.
 */
public class BusinessProfileActivity extends AppCompatActivity{
    // Firebase variables
    Firebase fbRef;

    // Variables for all UI elements
    private EditText shopName_ET;
    private EditText location_ET;
    private EditText email_ET;
    private EditText telephone_ET;
    private Spinner category_Spinner;
    private Button createBtn;
    private Button updateBtn;
    private Button logoutBtn;

    // Other variables
    private SharedPreferences prefs;
    private String phoneNo;
    private ArrayAdapter<String> adapter;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businessprofilepage);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise all UI elements first
        initialiseUIElements();

        // Initialise progress dialog, getSharedPreferences for this app, and firebase
        pd = new ProgressDialog(this);
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);
        fbRef = new Firebase(Constants.FIREBASE_MAIN);

        // add and implement text changed listener to email edit text
        email_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email_ET.setError(null);
            }
        });

        // add and implement text changed listener to email edit text
        shopName_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shopName_ET.setError(null);
            }
        });

        // set click listener to udpate and create button
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }
                if(!Commons.isEmailString(email_ET.getText().toString()))
                {
                    Commons.showToastMessage("Please provide valid email!", getApplicationContext());
                    email_ET.setError("Please provide valid email!");
                    return;
                }
                email_ET.setError(null);
                createShop();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }
                if(!Commons.isEmailString(email_ET.getText().toString()))
                {
                    Commons.showToastMessage("Please provide valid email!", getApplicationContext());
                    email_ET.setError("Please provide valid email!");
                    return;
                }
                email_ET.setError(null);
                updateShopInfo();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.logout(fbRef, BusinessProfileActivity.this);
            }
        });

        setEnableAllElements(false);
        loadCategoryList();
        getShopInfo();
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        shopName_ET = (EditText) findViewById(R.id.businessProf_shopName_ET);
        location_ET = (EditText) findViewById(R.id.businessProf_location_ET);
        email_ET = (EditText) findViewById(R.id.businessProf_email_ET);
        telephone_ET = (EditText) findViewById(R.id.businessProf_telephone_ET);
        category_Spinner = (Spinner) findViewById(R.id.businessProf_category_Spinner);
        createBtn = (Button) findViewById(R.id.businessProf_createbtn);
        updateBtn = (Button) findViewById(R.id.businessProf_updatebtn);
        logoutBtn = (Button) findViewById(R.id.businessProf_logoutbtn);
    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        shopName_ET.setEnabled(value);
        location_ET.setEnabled(value);
        email_ET.setEnabled(value);
        telephone_ET.setEnabled(value);
        category_Spinner.setEnabled(value);
        createBtn.setEnabled(value);
        updateBtn.setEnabled(value);
        logoutBtn.setEnabled(value);
    }

    /**
     * To create new shop
     */
    private void createShop()
    {
        Commons.showProgressDialog(pd, "Shop info", "Creating shop");
        final String shopname = shopName_ET.getText().toString();
        final String location = location_ET.getText().toString();
        final String email = email_ET.getText().toString();
        final String phoneno = telephone_ET.getText().toString();
        final String category = category_Spinner.getSelectedItem().toString().toLowerCase();
        phoneNo = prefs.getString(Constants.SHAREPREF_PHONE_NO, "");

        fbRef = new Firebase(Constants.FIREBASE_SHOPS);
        fbRef.child(shopname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.dismiss();
                if(dataSnapshot.getValue() != null)
                {
                    Commons.showToastMessage("Shop name is already taken!", getApplicationContext());
                    shopName_ET.setError("Shop name is already taken!");
                    setEnableAllElements(true);
                }
                else
                {
                    fbRef = new Firebase(Constants.FIREBASE_VENDOR);
                    fbRef.child(phoneNo).child("shops").child(shopname).setValue(shopname);

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("address", location);
                    map.put("category", category);
                    map.put("shop_name", shopname);
                    map.put("phoneno", phoneno);
                    map.put("email", email);
                    map.put("vendorid", phoneNo);

                    fbRef = new Firebase(Constants.FIREBASE_SHOPS);
                    fbRef.child(shopname).setValue(map);
                    getShopInfo();
                    Commons.showToastMessage("Shops created", getApplicationContext());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    private void updateShopInfo()
    {
        String shopname = shopName_ET.getText().toString();
        String location = location_ET.getText().toString();
        String email = email_ET.getText().toString();
        String phoneno = telephone_ET.getText().toString();
        String category = category_Spinner.getSelectedItem().toString().toLowerCase();
        phoneNo = prefs.getString(Constants.SHAREPREF_PHONE_NO, "");

        fbRef = new Firebase(Constants.FIREBASE_VENDOR);
        fbRef.child(phoneNo).child("shops").child(shopname).setValue(shopname);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("address", location);
        map.put("category", category);
        map.put("phoneno", phoneno);
        map.put("email", email);

        fbRef = new Firebase(Constants.FIREBASE_SHOPS);
        fbRef.child(shopname).updateChildren(map);
        getShopInfo();
        Commons.showToastMessage("Shops info udpated", getApplicationContext());
    }

    /**
     * Retrieve category from firebase and load it to category spinner
     */
    private void loadCategoryList()
    {
        // Check network connection
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            return;
        }
        Commons.showProgressDialog(pd, "Category list", "Loading category");
        fbRef = new Firebase(Constants.FIREBASE_CATEGORY);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> categoryList = new ArrayList<String>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String cat = ds.getValue().toString();
                    categoryList.add(Commons.firstLetterToUpper(cat));
                }

                // Create an ArrayAdapter using the string array and a default spinner layout
                adapter = new ArrayAdapter<String>(BusinessProfileActivity.this, android.R.layout.simple_spinner_item,
                        categoryList);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                category_Spinner.setAdapter(adapter);
                setEnableAllElements(true);
                pd.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * To get shop info from firebase that belongs to a vendor
     */
    private void getShopInfo()
    {
        // Check network connection
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            return;
        }
        Commons.showProgressDialog(pd, "Shop info", "Getting shop info");
        fbRef = new Firebase(Constants.FIREBASE_VENDOR);
        fbRef.child(prefs.getString(Constants.SHAREPREF_PHONE_NO, "96655485")).child("shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if no shop is created
                if (dataSnapshot.getValue() == null) {
                    pd.dismiss();
                    final AlertDialog alertDialog = new AlertDialog.Builder(BusinessProfileActivity.this).create();
                    alertDialog.setTitle("No shop");
                    alertDialog.setMessage("You have not created a shop.");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    createBtn.setVisibility(View.VISIBLE);
                    updateBtn.setVisibility(View.INVISIBLE);
                    email_ET.setText(prefs.getString(Constants.SHAREPREF_EMAIL, ""));
                    telephone_ET.setText(prefs.getString(Constants.SHAREPREF_PHONE_NO, ""));
                } else {
                    createBtn.setVisibility(View.INVISIBLE);
                    updateBtn.setVisibility(View.VISIBLE);
                    shopName_ET.setKeyListener(null); // set shopname_ET uneditable
                    String s = dataSnapshot.getKey();
                    loadShopInfo(dataSnapshot.getChildren().iterator().next().getValue().toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * Get all information about the shop
     * @param shopname the name of the shop
     */
    private void loadShopInfo(String shopname)
    {
        Commons.showProgressDialog(pd, "Shop info", "Loading shop info");
        fbRef = new Firebase(Constants.FIREBASE_SHOPS);
        fbRef.child(shopname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shopName_ET.setText(dataSnapshot.child("shop_name").getValue().toString());
                location_ET.setText(dataSnapshot.child("address").getValue().toString());
                email_ET.setText(dataSnapshot.child("email").getValue().toString());
                telephone_ET.setText(dataSnapshot.child("phoneno").getValue().toString());
                String cat = dataSnapshot.child("category").getValue().toString();
                category_Spinner.setSelection(adapter.getPosition(Commons.firstLetterToUpper(cat)));
                pd.dismiss();
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
            default:
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                break;
        }
        setEnableAllElements(true);
        pd.dismiss();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // To cancel and dismiss all current toast
        Commons.cancelToastMessage();
    }
}
