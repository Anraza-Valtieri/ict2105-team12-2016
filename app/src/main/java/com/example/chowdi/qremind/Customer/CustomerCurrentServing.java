package com.example.chowdi.qremind.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.QueueInfo;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.utils.QRCodeScanner;
import com.example.chowdi.qremind.views.CustomerMainNavDrawer;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CustomerCurrentServing extends BaseActivity {

    // Variable for Firebase
    private Firebase fbRefWaitingTime;
    private ValueEventListener waitingTimeListener = null;

    // Variable for all relevant UI elements
    private TextView vendorName_TV, currentlyServing_TV, myQueueNo_TV, waitingTime_TV;
    private Button time_ext_req_btn, claim_btn, refresh_btn;


    // Others variables
    private SharedPreferences prefs;
    private ProgressDialog pd;
    private String queueNo, queueKey, shopName, shopKey, customerid;

    // For QR Code camera
    private ZXingScannerView mScannerView;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customercurrentlyserving);
        setNavDrawer(new CustomerMainNavDrawer(this));

        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise getSharedPreferences for this app
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Get all strings passed from previous activity
        queueNo = prefs.getString(Constants.SHAREPREF_QUEUE_NO, null);
        queueKey = prefs.getString(Constants.SHAREPREF_QUEUE_KEY, null);
        shopName = prefs.getString(Constants.SHAREPREF_SHOP_NAME, null);
        shopKey = prefs.getString(Constants.SHAREPREF_SHOP_KEY, null);
        customerid = getQremindApplication().getCustomerUser().getPhoneno();

        loadQueueStats();
        getEstimatedWaitingTime();
        waitForTurn();

        // set listener to refresh button
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waitingTimeListener != null) {
                    fbRefWaitingTime.removeEventListener(waitingTimeListener);
                    waitingTimeListener = null;
                }
                getEstimatedWaitingTime();
            }
        });

        // set listener to claim button
        claim_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerCurrentServing.this, QRCodeScanner.class);
                startActivity(intent);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Wait for the camera starts up
                        SystemClock.sleep(1000);
                        while (!QRCodeScanner.scanningFinished) ;
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (QRCodeScanner.scanningCancelled)
                            return;
                        claimQueue(QRCodeScanner.result);
                    }
                }.execute();
            }
        });
    }

    private void claimQueue(final String queuekey)
    {
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            return;
        }

        if(!queuekey.equals(queueKey))
        {
            Commons.showToastMessage("Claim queue failed", getApplicationContext());
            return;
        }
        Firebase fbref = new Firebase(Constants.FIREBASE_CUSTOMER).child(customerid).child("current_queue");
        fbref.removeValue();
        fbref = new Firebase(Constants.FIREBASE_SHOPS).child(shopKey).child("queues").child(application.getCustomerUser().getPhoneno());
        fbref.removeValue();

        Firebase newFbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey).child(queueKey);
        newFbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GregorianCalendar datetime = new GregorianCalendar();
                String date = new SimpleDateFormat("yyyy/M/d").format(datetime.getTime());

                Firebase fbref = new Firebase(Constants.FIREBASE_SERVED_QUEUES).child(shopKey).child(date).child(queueKey);
                fbref.setValue(dataSnapshot.getValue());

                fbref = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey).child(queueKey);
                fbref.removeValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        fbRefQueueTurn.removeEventListener(queueTurnListener);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.SHAREPREF_QUEUE_KEY);
        editor.remove(Constants.SHAREPREF_SHOP_KEY);
        editor.remove(Constants.SHAREPREF_SHOP_NAME);
        editor.remove(Constants.SHAREPREF_QUEUE_NO);
        editor.commit();
        Intent intent = new Intent(this, CustomerHomePageActivity.class);
        startActivity(intent);
        Commons.showToastMessage("Queue claimed successfully", getApplicationContext());
        this.finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        fbRefWaitingTime.removeEventListener(waitingTimeListener);
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
     * To get estimated waiting time from firebase and show it to the user
     * There is a listener to Firebase to update the estimate time when there is on data changed in firebase for queues
     */
    private void getEstimatedWaitingTime()
    {
        Commons.showProgressDialog(pd, "Loading", "Calculating estimated waiting time.");
        fbRefWaitingTime = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey);
        waitingTimeListener = fbRefWaitingTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(queueKey).child("waiting_time").getValue() == null) {
                    String inQueuetime = dataSnapshot.child(queueKey).child("in_queue_time").getValue().toString();
                    estimateWaitingTime(Integer.parseInt(inQueuetime.split(":")[0]), Integer.parseInt(inQueuetime.split(":")[1]));
                } else {
                    String inQueuetime = dataSnapshot.child(queueKey).child("in_queue_time").getValue().toString();
                    int hours = Integer.parseInt(inQueuetime.split(":")[0]);
                    int minutes = Integer.parseInt(inQueuetime.split(":")[1]);
                    int waitingTime = Integer.parseInt(dataSnapshot.child(queueKey).child("waiting_time").getValue().toString());
                    if(waitingTime > 0) {
                        GregorianCalendar time = calcRemainingWaitingTime(waitingTime, hours, minutes);
                        dispRemainingTime(time);
                    }
                    else if (waitingTime <= 0)
                    {
                        estimateWaitingTime(Integer.parseInt(inQueuetime.split(":")[0]), Integer.parseInt(inQueuetime.split(":")[1]));
                    }
                    Commons.dismissProgressDialog(pd);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * To estimate waiting time before the user's turn
     * @param hours the hour that the queue number is enqueued
     * @param minutes the minute that the queue number is enqueued
     */
    private void estimateWaitingTime(final int hours, final int minutes) {
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                while (total == 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("calling").getValue() != null)
                            continue; // if this queue is already being called
                        int r = new Random().nextInt(13) + 5;
                        total += (r > 13) ? 13 : r; // for each queue add at least 5 minutes and at most 13 mins
                        if (ds.getKey().equals(queueKey)) {
                            break;
                        }
                    }
                }
                GregorianCalendar time = calcRemainingWaitingTime(total, hours, minutes);
                dispRemainingTime(time);
                new Firebase(Constants.FIREBASE_QUEUES).child(shopKey).child(queueKey).child("waiting_time").setValue(
                        (time.get(Calendar.HOUR) * 60) + time.get(Calendar.MINUTE));
                Commons.dismissProgressDialog(pd);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * To display the remaining time for the queue's turn
     * @param time GregorianCalendar that stores the remaining hours and minutes
     */
    private void dispRemainingTime(GregorianCalendar time)
    {
        String hrsStr = new SimpleDateFormat("hh").format(time.getTime()) + " hrs ";
        String minsStr = new SimpleDateFormat("mm").format(time.getTime()) + " mins";

        waitingTime_TV.setText(
                ((time.get(Calendar.HOUR) <= 0) && (time.get(Calendar.MINUTE) <= 0)) ?
                        ("Your turn's coming.") // if no remaining time left
                        : // if still got remaining time. If hours > 0 then display hours else not display hours
                        (((time.get(Calendar.HOUR) > 0) ? hrsStr : "") + minsStr)
        );
    }

    /**
     * To calculate the remaining waiting time before the customer's queue's turn
     * @param waitingTime the initial waiting time given when the queue is created
     * @param hours the hour that the queue is created
     * @param minutes the minute that the queue is created
     * @return GregorianCalendar return the calculated remaining time
     */
    private GregorianCalendar calcRemainingWaitingTime(int waitingTime, int hours, int minutes)
    {
        hours += waitingTime/60;
        minutes += waitingTime%60;
        if(minutes >= 60)
        {
            hours += minutes/60;
            minutes = minutes%60;
        }

        hours -= new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
        minutes -= new GregorianCalendar().get(Calendar.MINUTE);

        if(hours < 0 || minutes <= 0) // if there is no remaining time left
            return new GregorianCalendar(0,0,0,0,0);

        int remainingTime = (hours * 60) + minutes; // remaining time in minutes
        hours = remainingTime / 60;
        minutes = remainingTime % 60;

        return new GregorianCalendar(0,0,0,hours,minutes);
    }

    /**
     * To set a listener to firebase queues for checking user's turns
     */
    private void waitForTurn()
    {
        if(fbRefQueueTurn != null)
        {
            fbRefQueueTurn.removeEventListener(queueTurnListener);
        }
        fbRefQueueTurn = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey).child(queueKey);
        queueTurnListener = fbRefQueueTurn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    QueueInfo queueInfo = dataSnapshot.getValue(QueueInfo.class);
                    if(queueInfo.getCalling() != null) {
                        Commons.dismissProgressDialog(pd);
                        fbRefWaitingTime.removeEventListener(waitingTimeListener);
                        waitingTime_TV.setText("Your turn's up!");

                        refresh_btn.setVisibility(View.INVISIBLE);
                        claim_btn.setVisibility(View.VISIBLE);
                        if(!notificationPoppedOut)
                            popUpNotification(queueNo);
                        if(!application.notificationSend)
                            application.showNotification();
                    }
                }
                else
                {
                    fbRefWaitingTime.removeEventListener(waitingTimeListener);
                    fbRefQueueTurn.removeEventListener(queueTurnListener);
                    Intent intent = new Intent(CustomerCurrentServing.this, CustomerHomePageActivity.class);
                    startActivity(intent);
                    Commons.showToastMessage("You have been removed from queue!", getApplicationContext());
                    CustomerCurrentServing.this.finish();
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
