package com.example.chowdi.qremind;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ListView;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerHomePageActivity extends AppCompatActivity{
    //Variable for Firebase
    private Firebase firebase;
    private SharedPreferences sharedPreferences;

    //public static ArrayList categories;
    private ArrayList<String> categories = new ArrayList<String>();
    private String[] ratings = {"1","2", "3","4","5"};
    private Spinner spinnerCategory, spinnerRatings;
    private String userSelectCategory,userSelectRatings, shopName,phoneNumber,email,ratingsOfShop,categoryOfShop;
    //private TextView shopNameTV;
    private RecyclerView rv;
    private List<Shop> shops;
    private ListView listView;
    private TextView shopNameTV,categoryTV,phoneNumberTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerhomepage);
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        spinnerRatings = (Spinner) findViewById((R.id.spinner_ratings));
        //listView = (ListView) findViewById(R.id.listView);

        shopNameTV = (TextView) findViewById(R.id.shopnameTV);
        categoryTV = (TextView) findViewById(R.id.categoryTV);
        phoneNumberTV = (TextView) findViewById(R.id.phoneNumberTV);

        /* Recycler View Initialization*/
        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        //rv.setHasFixedSize(true);


        //Initialize Firebase library in Android context before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        getCategories();

        /* Setting the Listener for the category spinner */
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                userSelectCategory = String.valueOf(parent.getItemAtPosition(pos));

                /* Calls the getShops() function and populates the category spinner with data from Firebase */
                getShops();

            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Another Interface callback
            }
        });

        /* Initializing the ratings spinner*/
        setRatings();
        spinnerRatings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.d("pass short_test_1", "point  reached");
                userSelectRatings = String.valueOf(parent.getItemAtPosition(pos));
                getRatings();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Another Interface callback
            }
        });

    }
    /* Initializing the */
    private void getCategories() {
        firebase = new Firebase(Constants.FIREBASE_CATEGORY);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    categories.add(ds.getValue().toString());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerHomePageActivity.this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void setRatings() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerHomePageActivity.this,
                android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRatings.setAdapter(adapter);
    }

    private void getRatings() {
        firebase = new Firebase("https://qremind1.firebaseio.com/shop_test");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shops = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.child("ratings").getValue().toString().equals(userSelectRatings)) {
                        shopName = ds.child("shop_name").getValue().toString();
                        phoneNumber = ds.child("phone_no").getValue().toString();
                        categoryOfShop = ds.child("category").getValue().toString();
                        email = ds.child("email").getValue().toString();
                        shops.add(new Shop(shopName,categoryOfShop,phoneNumber,userSelectRatings,email));
                        initializeAdapter();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    private void getShops() {
        //firebase = new Firebase(Constants.FIREBASE_SHOPS);
        firebase = new Firebase("https://qremind1.firebaseio.com/shop_test");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shops = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.child("category").getValue().toString().equals(userSelectCategory)) {
                        shopName = ds.child("shop_name").getValue().toString();
                        phoneNumber = ds.child("phone_no").getValue().toString();
                        ratingsOfShop = ds.child("ratings").getValue().toString();
                        email = ds.child("email").getValue().toString();
                        shops.add(new Shop(shopName,userSelectCategory,phoneNumber,ratingsOfShop,email));
                        initializeAdapter();
                    }

                    else {
                        //what to display when there are no records?


                    }
                    // Log.d("pass short_test_1", "if loop failed/");
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(shops);
        rv.setAdapter(adapter);
    }
}
