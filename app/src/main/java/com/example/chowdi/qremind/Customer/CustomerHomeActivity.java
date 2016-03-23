package com.example.chowdi.qremind.Customer;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chowdi.qremind.BusinessProfileActivity;
import com.example.chowdi.qremind.Login_RegisterActivity;
import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.models.Shop;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

public class CustomerHomeActivity extends AppCompatActivity {
    private String TAG = CustomerHomeActivity.class.getSimpleName();
    private ListView custShopsListView;
    private ArrayAdapter<String> listAdapter;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    protected ArrayList<String> shopNameList;
    protected FirebaseListAdapter<Shop> fbAdapter;
    private String custID = "91513429";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerhomepage);
        //Firebase stuff
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase(Constants.FIREBASE_SHOPS);
        //bind listview
      //  custShopsListView = (ListView)findViewById(R.id.FandB_listView);

        // Create and populate names.
        shopNameList = new ArrayList<String>();

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        // Create ArrayAdapter using the planet list.
        //listAdapter = new ArrayAdapter<String>(this, R.layout.list_row_customerhomepage, shopNameList);

        // Set the ArrayAdapter as the ListView's adapter.
        //custShopsListView.setAdapter(listAdapter);
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

        Commons.addDrawerItems(this, mDrawerList);
        mDrawerToggle = Commons.setupDrawer(this, this.mDrawerLayout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
