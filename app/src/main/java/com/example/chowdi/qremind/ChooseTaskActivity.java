package com.example.chowdi.qremind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.chowdi.qremind.Vendor.BusinessProfileActivity;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.views.VendorMainNavDrawer;
import com.firebase.client.Firebase;

public class ChooseTaskActivity extends BaseActivity{

    // Firebase variables
    Firebase fbRef;

    // Variables for all UI elements
    private Button manageBtn;
    private Button settingsBtn;
    private Button logoutBtn;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosetask);

        // Initialise firebase
        fbRef = new Firebase(Constants.FIREBASE_MAIN);

        // Initialise all UI elements first
        initialiseUIElements();

        // Create navigation sidebar
        setNavDrawer(new VendorMainNavDrawer(this));
//        Commons.addDrawerItems(this, mDrawerList);
//        mDrawerToggle = Commons.setupDrawer(this, this.mDrawerLayout);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

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
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
    }

    /**
     * Sync the toggle state of the Navigation Sidebar after onCreate has occurred
     * @param savedInstanceState state of the Activity
     */
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
//    }

    /**
     * Handle the clicking of an item in the navigation sidebar
     * If successfully handled, return true
     * else return false which is the default implementation
     * @param item clickable options in the navigation sidebar
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
//    }
}
