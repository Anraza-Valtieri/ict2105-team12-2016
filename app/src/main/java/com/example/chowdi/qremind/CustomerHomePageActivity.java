package com.example.chowdi.qremind;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chowdi on 2/3/2016.
 */
public class CustomerHomePageActivity extends AppCompatActivity{
    //Variable for Firebase
    private Firebase firebase;
    private SharedPreferences sharedPreferences;

    //public static ArrayList categories;
    private ArrayList<String> categories = new ArrayList<String>();
    private String[] ratings = {"1 Star","2 Stars", "3 Stars","4 Stars","5 Stars"};
    private Spinner spinnerCategory, spinnerRatings;
    private String userSelection, shopName;
    private TextView shopNameTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_customerhomepage);

        setContentView(R.layout.activity_customerhomepage);

        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        spinnerRatings = (Spinner) findViewById((R.id.spinner_ratings));
        shopNameTV = (TextView) findViewById(R.id.shopName);
        shopName = shopNameTV.toString();




        //Initialize Firebase library in Android context before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        getCategories();
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent,View view,int pos,long id) {
                userSelection = String.valueOf(parent.getItemIdAtPosition(pos));
                //getShops();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Another Interface callback
            }


        });
        getRatings();


    }

    private void getCategories() {
        firebase = new Firebase(Constants.FIREBASE_CATEGORY);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    categories.add(ds.getValue().toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerHomePageActivity.this,
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getRatings() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerHomePageActivity.this,
                android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRatings.setAdapter(adapter);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item usinG parent.getItemAtPosition(pos)
        userSelection = String.valueOf(parent.getItemIdAtPosition(pos));
        getShops();


    }

    public void onNothingSelected(AdapterView<?> parent) {
        //Another Interface callback
    }

    private void getShops() {
        firebase = new Firebase("FIREBASE_SHOPS");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.child("category").getValue().toString().equals(userSelection)) {
                        shopName = ds.child("shop_name").getValue().toString();
                        shopNameTV.setText("shopName");
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
