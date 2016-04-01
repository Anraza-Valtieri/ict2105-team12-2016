package com.example.chowdi.qremind.Vendor;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.QueueInfo;
import com.example.chowdi.qremind.infrastructure.Vendor;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by lotus on 4/1/2016.
 */
public class ListOfCustomersInQueueActivity extends BaseActivity{
    private RecyclerView rv;
    private CustomerListAdapter adapter;
    private Firebase firebase;
    private Vendor vendor;
    private Firebase firebaseQueueRef;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.list_of_customers_in_queue_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv =(RecyclerView) findViewById(R.id.activity_vendor_customersInQueue_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));//layout to layout the items in RV

        adapter = new CustomerListAdapter();
        rv.setAdapter(adapter);
        //initialize queue infos
        vendor = application.getVendorUser();
        if(vendor.getShops() != null)
            populateQueueInfoAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateQueueInfoAdapter() {
        //firebase = new Firebase(Constants.FIREBASE_SHOPS);
        firebase = new Firebase(Constants.FIREBASE_QUEUES);
        firebaseQueueRef = firebase.child(vendor.getShops().values().toArray()[0].toString());
        firebaseQueueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear previous data.

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    QueueInfo queueInfo = ds.getValue(QueueInfo.class);
                    adapter.addQueueInfo(queueInfo);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    //RV adapter private class and view holder
    private class CustomerListAdapter extends RecyclerView.Adapter<CustomerListViewHolder>{

        private final ArrayList<QueueInfo> queueInfoArrayList;

        public CustomerListAdapter(){
            queueInfoArrayList = new ArrayList<>();
        }

        public void addQueueInfo(QueueInfo queueInfo){
            queueInfoArrayList.add(queueInfo);
            notifyItemInserted(queueInfoArrayList.size()-1);//insert at last

        }

        public void removeQueueInfo(QueueInfo queueInfo){
            int position = queueInfoArrayList.indexOf(queueInfo);
            if(position == -1){//prevent double click
                return;
            }
            queueInfoArrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, queueInfoArrayList.size());
        }
        //create view,layout
        @Override
        public CustomerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = getLayoutInflater().inflate(R.layout.list_customer_in_queue, parent, false);//layout inflater from the activity this class is in, the passed layout is given to the viewholder to inflate individual views
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QueueInfo qInfo = (QueueInfo) view.getTag();
                    removeQueueInfo(qInfo);
                }
            });

            return new CustomerListViewHolder(view);
        }

        //populate data
        @Override
        public void onBindViewHolder(CustomerListViewHolder holder, int position) {
            QueueInfo qInfo = queueInfoArrayList.get(position);
            holder.itemView.setTag(qInfo);//tag each view to the DTO
            holder.queueNoTextView.setText(Integer.toString(qInfo.getQueue_no()));
            holder.inQueueDateTextView.setText(qInfo.getIn_queue_date());
            holder.inQueueTimeTextView.setText(qInfo.getIn_queue_time());

        }

        @Override
        public int getItemCount() {
            return queueInfoArrayList.size();
        }
    }

    private class CustomerListViewHolder extends RecyclerView.ViewHolder{
        public TextView queueNoTextView;
        public TextView inQueueDateTextView;
        public TextView inQueueTimeTextView;

        public CustomerListViewHolder(View itemView) {
            super(itemView);
            queueNoTextView = (TextView)itemView.findViewById(R.id.list_item_customer_queueNo);
            inQueueDateTextView = (TextView)itemView.findViewById(R.id.list_item_customer_in_queue_date);
            inQueueTimeTextView = (TextView)itemView.findViewById(R.id.list_item_customer_in_queue_time);

        }
    }
}
