package com.example.chowdi.qremind.Customer;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CustomerCurrentServing extends AppCompatActivity {

    // Variable for Firebase
    private Firebase fbRefWaitingTime;
    private Firebase fbRefQueueTurn;
    private ValueEventListener waitingTimeListener = null, queueTurnListener = null;

    // Variable for all relevant UI elements
    private TextView vendorName_TV, currentlyServing_TV, myQueueNo_TV, waitingTime_TV;
    private Button time_ext_req_btn, claim_btn, refresh_btn;

    // Others variables
    private SharedPreferences prefs;
    private ProgressDialog pd;
    private static int lastWaitingTime = -1;
    private String queueNo, queueKey, shopName, shopKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customercurrentlyserving);

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise getSharedPreferences for this app
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Get all strings passed from previous activity
        queueNo = String.valueOf(getIntent().getExtras().getInt(Constants.EX_MSG_QUEUE_NO));
        queueKey = getIntent().getExtras().getString(Constants.EX_MSG_QUEUE_KEY);
        shopName = getIntent().getExtras().getString(Constants.EX_MSG_SHOP_NAME);
        shopKey = getIntent().getExtras().getString(Constants.EX_MSG_SHOP_KEY);

        loadQueueStats();
        estimateWaitingTime();
        waitForTurn();
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        vendorName_TV = (TextView)findViewById(R.id.vendorname_TV);
        currentlyServing_TV = (TextView)findViewById(R.id.currentServing_TV);
        myQueueNo_TV = (TextView)findViewById(R.id.queue_number_TV);
        waitingTime_TV = (TextView)findViewById(R.id.waiting_time_TV);
        time_ext_req_btn = (Button) findViewById(R.id.time_ext_req_btn);
        claim_btn = (Button) findViewById(R.id.claim_btn);
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
    }

    /**
     * To display the relevant information to UI
     */
    private void loadQueueStats(){
        vendorName_TV.setText(shopName);
        myQueueNo_TV.setText(queueNo);
    }

    /**
     * To calculate estimate waiting time before the user's turn
     * There is a listener to Firebase to update the estimate time when there is on data changed in firebase for queues
     */
    private void estimateWaitingTime()
    {
        fbRefWaitingTime = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey);
        waitingTimeListener = fbRefWaitingTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                do {
                    total = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        int r = new Random().nextInt(13) + 5;
                        total += (r>13)? 13 : r; // for each queue add 13 minutes
                        if (ds.getKey().equals(queueKey)) {
                            break;
                        }
                    }
                } while (total > lastWaitingTime && lastWaitingTime != -1);

                int hours = total / 60;
                int minutes = total % 60;
                waitingTime_TV.setText(((hours > 0) ? hours + " hrs " : "") + minutes + " mins");
                lastWaitingTime = total;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * To set a listener to firebase queues for checking user's turns
     */
    private void waitForTurn()
    {
        fbRefQueueTurn = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey).child(queueKey).child("calling");
        queueTurnListener = fbRefQueueTurn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    if((boolean) dataSnapshot.getValue() == true)
                    {
                        fbRefWaitingTime.removeEventListener(waitingTimeListener);
                        waitingTime_TV.setText("Your turn's up!");
                        refresh_btn.setVisibility(View.INVISIBLE);
                        claim_btn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * To handle all kind of firebase errors where to show the appropriate
     * and correct error messages on each errors
     * @param firebaseError FirebaseError
     */
    private void handleFirebaseError(FirebaseError firebaseError)
    {
        switch (firebaseError.getCode())
        {
            default:
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                break;
        }
        Commons.dismissProgressDialog(pd);
    }
}
