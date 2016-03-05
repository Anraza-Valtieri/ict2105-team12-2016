package com.example.chowdi.qremind;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by chowdi on 2/3/2016.
 */
public class GetQueueNumberActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getqueuenumber);

        Button NoOfPax = (Button) findViewById(R.id.bGetQNo);

        NoOfPax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codes to issue a queue number to the client based on the no. of pax entered.
                //then send to new view.


            }
        });
    }


}
