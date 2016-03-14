package com.example.chowdi.qremind.Customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class CustomerGetQueue extends AppCompatActivity {
    TextView vendorName;
    Button getQueueBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getqueuenumber);
        vendorName = (TextView)findViewById(R.id.getqueueno_from_vendor);
        getQueueBtn = (Button)findViewById(R.id.getqueuebtn);


        vendorName.setText(getIntent().getExtras().getString("SHOP_NAME"));
        getQueueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGetQueue(v);
            }
        });
    }

    /**
     * Inserts into shop queue node,and customer current queue
     * @param view
     */
    public void onClickGetQueue(View view){
        Firebase ref = new Firebase(Constants.FIREBASE_SHOPS+"/"+getIntent().getExtras().getString("SHOP_NAME")+"/queues");
        Firebase custRef = new Firebase(Constants.FIREBASE_CUSTOMER+"/"+getIntent().getExtras().getString("CUSTOMER_ID")+"/current_queue");
        //add shop queue
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(getIntent().getExtras().getString("CUSTOMER_ID"), true);
        ref.updateChildren(map);
        //add cust current queue
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("no", 5);
        map2.put(getIntent().getExtras().getString("SHOP_NAME"), true);
        custRef.setValue(map2);
    }
}
