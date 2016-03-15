package com.example.chowdi.qremind.Customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;

public class CustomerCurrentServing extends AppCompatActivity {
    private String shop_name;
    private String customer_id;
    private TextView vendorName,currentlyServing,myQueueNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customercurrentlyserving);
        shop_name = getIntent().getExtras().getString("SHOP_NAME");
        customer_id = getIntent().getExtras().getString("CUSTOMER_ID");

        vendorName = (TextView)findViewById(R.id.vendorname_textView);
        currentlyServing = (TextView)findViewById(R.id.textViewServing);
        myQueueNo = (TextView)findViewById(R.id.textViewMyQueueNo);

        vendorName.setText(shop_name);

    }

    private void loadQueueStats(){
        Firebase ref = new Firebase(Constants.FIREBASE_SHOPS+"/"+getIntent().getExtras().getString("SHOP_NAME")+"/queues");
        Firebase custRef = new Firebase(Constants.FIREBASE_CUSTOMER+"/"+getIntent().getExtras().getString("CUSTOMER_ID")+"/current_queue");
    }
}
