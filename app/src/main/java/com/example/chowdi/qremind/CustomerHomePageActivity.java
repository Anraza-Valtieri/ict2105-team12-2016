package com.example.chowdi.qremind;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
    private Spinner spinnerCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerhomepage);
        //String[] categories;

        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);

        //Initialize Firebase library in Android context before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        getCategories();
        //converting the arraylist to array to be used in the ArrayAdapter


    }

    private void getCategories() {
        firebase = new Firebase(Constants.FIREBASE_CATEGORY);
        firebase.child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    categories.add(ds.getValue().toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerHomePageActivity.this,android.R.layout.simple_spinner_item, (String[])categories.toArray());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
