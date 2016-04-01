package com.example.chowdi.qremind.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Customer;
import com.example.chowdi.qremind.infrastructure.QueueInfo;
import com.example.chowdi.qremind.infrastructure.Shop;
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
    private Customer user;
    private Shop shop;
    private QueueInfo queueInfo;
    private Boolean shopRetrieved, queueRetrieved;

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
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK, MODE_PRIVATE);

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Get all strings
        user = application.getCustomerUser();

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute()
            {
                setEnableAllElements(false);
            }

            @Override
            protected Void doInBackground(Void... params)
            {
                shopRetrieved = false;
                queueRetrieved = false;

                getShopInfo();
                getQueueInfo();

                while(!shopRetrieved || !queueRetrieved)
                {
                    SystemClock.sleep(1000);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                loadQueueStats();
                waitForTurn();
                setEnableAllElements(true);
            }
        }.execute();

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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(fbRefWaitingTime != null)
            fbRefWaitingTime.removeEventListener(waitingTimeListener);
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        vendorName_TV = (TextView)findViewById(R.id.vendorname_TV);
        currentlyServing_TV = (TextView)findViewById(R.id.remainingQueue_TV);
        myQueueNo_TV = (TextView)findViewById(R.id.queue_number_TV);
        waitingTime_TV = (TextView)findViewById(R.id.waiting_time_TV);
        time_ext_req_btn = (Button) findViewById(R.id.leave_queue_btn);
        claim_btn = (Button) findViewById(R.id.claim_btn);
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        vendorName_TV.setEnabled(value);
        currentlyServing_TV.setEnabled(value);
        myQueueNo_TV.setEnabled(value);
        waitingTime_TV.setEnabled(value);
        time_ext_req_btn.setEnabled(value);
        claim_btn.setEnabled(value);
        refresh_btn.setEnabled(value);
    }

    /**
     * To display the relevant information to UI
     */
    private void loadQueueStats(){
        vendorName_TV.setText(shop.getShop_name());
        myQueueNo_TV.setText(queueInfo.getQueue_no()+"");
    }

    /**
     * To get estimated waiting time from firebase and show it to the user
     * There is a listener to Firebase to update the estimate time when there is on data changed in firebase for queues
     */
    private void getEstimatedWaitingTime()
    {
        Commons.showProgressDialog(pd, "Loading", "Calculating estimated waiting time.");
        fbRefWaitingTime = new Firebase(Constants.FIREBASE_QUEUES +"/"+ shop.getShop_key());
        waitingTimeListener = fbRefWaitingTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(queueInfo.getQueue_key()).child("waiting_time").getValue() == null) {
                    String inQueuetime = "";
                    try {
                        inQueuetime = dataSnapshot.child(queueInfo.getQueue_key()).child("in_queue_time").getValue().toString();
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                        return;
                    }
                    estimateWaitingTime(Integer.parseInt(inQueuetime.split(":")[0]), Integer.parseInt(inQueuetime.split(":")[1]));
                } else {
                    if(queueInfo.getCalling() != null) return;
                    String inQueuetime = dataSnapshot.child(queueInfo.getQueue_key()).child("in_queue_time").getValue().toString();
                    int hours = Integer.parseInt(inQueuetime.split(":")[0]);
                    int minutes = Integer.parseInt(inQueuetime.split(":")[1]);
                    int waitingTime = Integer.parseInt(dataSnapshot.child(queueInfo.getQueue_key()).child("waiting_time").getValue().toString());
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
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key());
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
                        if (ds.getKey().equals(queueInfo.getQueue_key())) {
                            break;
                        }
                    }
                }
                GregorianCalendar time = calcRemainingWaitingTime(total, hours, minutes);
                dispRemainingTime(time);
                new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key()).child(queueInfo.getQueue_key()).child("waiting_time").setValue(
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
        fbRefQueueTurn = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key()).child(queueInfo.getQueue_key());
        queueTurnListener = fbRefQueueTurn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    String queuekey = user.getCurrent_queue().get("queue_key").toString();
                    queueInfo = dataSnapshot.getValue(QueueInfo.class);
                    queueInfo.setQueue_key(queuekey);
                    if(queueInfo.getCalling() != null) {
                        Commons.dismissProgressDialog(pd);
                        if(fbRefWaitingTime!=null)
                            fbRefWaitingTime.removeEventListener(waitingTimeListener);
                        waitingTime_TV.setText("Your turn's up!");

                        refresh_btn.setVisibility(View.INVISIBLE);
                        claim_btn.setVisibility(View.VISIBLE);
                        if(!notificationPoppedOut)
                            popUpNotification(queueInfo.getQueue_no()+"");
                        if(!application.notificationSend)
                            application.showNotification();
                        return;
                    }
                    getEstimatedWaitingTime();
                }
                else
                {
                    if(fbRefWaitingTime!=null)
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
     * To claim the queue after scanning QR code
     * @param queuekey queue key retrieved from QR code
     */
    private void claimQueue(final String queuekey)
    {
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            return;
        }

        if(!queuekey.equals(queueInfo.getQueue_key()))
        {
            Commons.showToastMessage("Claim queue failed", getApplicationContext());
            return;
        }
        Firebase fbref = new Firebase(Constants.FIREBASE_CUSTOMER).child(user.getPhoneno()).child("current_queue");
        fbref.removeValue();
        fbref = new Firebase(Constants.FIREBASE_SHOPS).child(shop.getShop_key()).child("queues").child(application.getCustomerUser().getPhoneno());
        fbref.removeValue();

        Firebase newFbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key()).child(queueInfo.getQueue_key());
        newFbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GregorianCalendar datetime = new GregorianCalendar();
                String date = new SimpleDateFormat("yyyy/M/d").format(datetime.getTime());

                Firebase fbref = new Firebase(Constants.FIREBASE_SERVED_QUEUES).child(shop.getShop_key()).child(date).child(queueInfo.getQueue_key());
                fbref.setValue(dataSnapshot.getValue());

                fbref = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key()).child(queueInfo.getQueue_key());
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

    /**
     * To get shop info from firebase
     */
    private void getShopInfo()
    {
        final String shopkey = user.getCurrent_queue().get("shop").toString();
        Firebase fbRef = new Firebase(Constants.FIREBASE_SHOPS).child(shopkey);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    shop = dataSnapshot.getValue(Shop.class);
                    shop.setShop_key(shopkey);
                }
                shopRetrieved = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
            }
        });
    }

    /**
     * To get current queue info from firebase
     */
    private void getQueueInfo()
    {
        final String queuekey = user.getCurrent_queue().get("queue_key").toString();
        String shopkey = user.getCurrent_queue().get("shop").toString();
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shopkey).child(queuekey);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    queueInfo = dataSnapshot.getValue(QueueInfo.class);
                    queueInfo.setQueue_key(queuekey);
                }
                queueRetrieved = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
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
