package com.example.chowdi.qremind.Customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;

public class S3Task4_1 extends AppCompatActivity {

    private Button btnLeaveQueue;
    private String user_hp = "96655485";
    private String shop_user_in = "test_one";
    private String queue_user_in ="-KDES4vrsxJr5N1osqad";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3_task4_1);
        Firebase.setAndroidContext(this);





        btnLeaveQueue = (Button)findViewById(R.id.buttonLeaveQueue);
        btnLeaveQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove from user
                Firebase ref = new Firebase(Constants.FIREBASE_CUSTOMER+"/"+user_hp+"/current_queue");
                ref.setValue(null);

                //remove from shop
                Firebase ref2 = new Firebase(Constants.FIREBASE_QUEUES);
                Firebase shopRef = ref2.child(shop_user_in+"/"+queue_user_in);
                shopRef.setValue(null);
            }
        });
    }

}
