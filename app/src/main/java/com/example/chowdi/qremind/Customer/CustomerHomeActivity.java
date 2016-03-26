package com.example.chowdi.qremind.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Shop;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.views.CustomerMainNavDrawer;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

public class CustomerHomeActivity extends BaseActivity {

    private String TAG = CustomerHomeActivity.class.getSimpleName();

    private ListView custShopsListView, mDrawerList;
    private ArrayAdapter<String> listAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    protected ArrayList<String> shopNameList;
    protected FirebaseListAdapter<Shop> fbAdapter;
    private String custID = "91513429";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerhomepage);
        setNavDrawer(new CustomerMainNavDrawer(this));
        //Firebase stuff
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase(Constants.FIREBASE_SHOPS);

        // Create and populate names.
        shopNameList = new ArrayList<String>();
        fbAdapter = new CustomerShopListAdapter(this,Shop.class,R.layout.list_row_customerhomepage,ref);
        custShopsListView.setAdapter(fbAdapter);
        custShopsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Shop clickedShop = (Shop) parent.getItemAtPosition(position);
                Log.d(TAG, "onItemClick: " + clickedShop.getShop_name());
                //start get queue number activity
                Intent intent = new Intent(getBaseContext(), CustomerGetQueueActivity.class);
                intent.putExtra("SHOP_NAME", clickedShop.getShop_name());
                intent.putExtra("CUSTOMER_ID", custID);
                startActivity(intent);
            }
        });


    }



}
