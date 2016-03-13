package com.example.chowdi.qremind.business_activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.models.Shop;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class BusinessQueueDashboard extends AppCompatActivity {

    RecyclerView recycler;
    FirebaseRecyclerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getApplicationContext());
        setContentView(R.layout.queuedashboard_activity);
        recycler = (RecyclerView)findViewById(R.id.queuedashboardRV);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        Firebase refBase = new Firebase(Constants.VENDOR_QUEUES);
        mAdapter = new FirebaseRecyclerAdapter<Shop, ShopViewHolder>(Shop.class, R.layout.queuedashboard_activity_cardview, ShopViewHolder.class, refBase) {
            @Override
            public void populateViewHolder(ShopViewHolder view, Shop chatMessage, int position) {
                view.noOfCustomers.setText("xcsafdds"+chatMessage.getCategory());
            }
        };
        recycler.setAdapter(mAdapter);
    }





    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView noOfCustomers;

        public ShopViewHolder(View itemView){
            super(itemView);
            noOfCustomers = (TextView)itemView.findViewById(R.id.txtNoOfCustomers);
        }
    }
}
