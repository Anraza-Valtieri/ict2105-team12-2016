package com.example.chowdi.qremind.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerGetQueueActivity extends AppCompatActivity {

    // Variable for Firebase
    private Firebase fbRef;

    // Variable for all relevant UI elements
    private TextView shopnameTV;
    private EditText paxNoET;
    private Button getQueueBtn;

    // Others variables
    private SharedPreferences prefs;
    private ProgressDialog pd;
    private String shop_key;
    private String shop_name;
    private String customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getqueuenumber);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Initialise getSharedPreferences for this app
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);

        // Retrieve information passed from previous intent
        shop_key = "test_one"; //getIntent().getExtras().getString("SHOP_KEY");
        shop_name = "Test One"; //getIntent().getExtras().getString("SHOP_NAME");
        customer_id = "96655485"; //getIntent().getExtras().getString("CUSTOMER_ID");

        // display the shopname
        shopnameTV.setText(shop_name);
        getQueueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQueue();
            }
        });
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        shopnameTV = (TextView)findViewById(R.id.custGetQueue_shopname_TV);
        getQueueBtn = (Button)findViewById(R.id.custGetQueue_getqueue_btn);
        paxNoET = (EditText) findViewById(R.id.custGetQueue_paxnumber_ET);
    }

    /**
     * Inserts into shop queue node, and customer current queue
     */
    public void getQueue(){
        Commons.showProgressDialog(pd, "Please wait", "Getting queue number.");

        Firebase vendorRef = new Firebase(Constants.FIREBASE_SHOPS + "/" + shop_key + "/queues");
        Firebase custRef = new Firebase(Constants.FIREBASE_CUSTOMER +"/"+ customer_id +"/current_queue");
        Firebase queueRef = new Firebase(Constants.FIREBASE_QUEUES +"/"+ shop_key).push();

        // map for queues
        Map<String, Object> qMap = new HashMap<String, Object>();
        qMap.put("customer_id", customer_id);
        qMap.put("connected", true);
        qMap.put("current_location", "nil");
        qMap.put("in_queue_date", new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        qMap.put("in_queue_time", new SimpleDateFormat("hh:mm").format(new Date()));
        qMap.put("time_ext_requested", false);
        queueRef.setValue(qMap); // insert into queues

        String queueKey = queueRef.getKey();
        int queueNo = Commons.keyToNoConverter(queueKey);
        queueRef.child("queue_no").setValue(queueNo); //update the queue no

        // map for customer current queue
        Map<String, Object> custMap = new HashMap<String, Object>();
        custMap.put("queue_key", queueKey);
        custMap.put("shop", shop_key);
        custRef.setValue(custMap); // insert into queues

        // map for customer current queue
        Map<String, Object> vendorMap = new HashMap<String, Object>();
        vendorMap.put(customer_id, true);
        vendorRef.setValue(vendorMap); // insert into queues

        Commons.dismissProgressDialog(pd);

        Intent intent = new Intent(CustomerGetQueueActivity.this, CustomerCurrentServing.class);
        intent.putExtra(Constants.EX_MSG_SHOP_NAME, shop_name);
        intent.putExtra(Constants.EX_MSG_SHOP_KEY, shop_key);
        intent.putExtra(Constants.EX_MSG_CUSTOMER_ID, customer_id);
        intent.putExtra(Constants.EX_MSG_QUEUE_KEY, queueKey);
        intent.putExtra(Constants.EX_MSG_QUEUE_NO, queueNo);
        startActivity(intent);
        CustomerGetQueueActivity.this.finish();
    }
}