package com.example.anton.test_application.Business;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.anton.test_application.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 3/2/2016.
 */
public class QueueDashboardActivity extends AppCompatActivity{
    Firebase myFirebaseRef;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<QueueInfo> qInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queuedashboard_activity);
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://qremind1.firebaseio.com/AppUserAccount");

        mRecyclerView = (RecyclerView) findViewById(R.id.queuedashboardRV);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initializeData();
        initializeAdapter();



    }
    public void onClickFireBaseAdd(View v){
        writeData();
    }
    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeAdapter(){
        QueueInfoAdapter adapter = new QueueInfoAdapter(qInfo,mRecyclerView);
        mRecyclerView.setAdapter(adapter);
    }
    private void initializeData(){
        qInfo =  new ArrayList<>();
        qInfo.add(new QueueInfo("No of People In Queue", 28));
        qInfo.add(new QueueInfo("Estimated Wait time in minutes", 5));
    }
    public void writeData(){
        myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");
        qInfo.get(0).noOfCustomers++;
    }
}
