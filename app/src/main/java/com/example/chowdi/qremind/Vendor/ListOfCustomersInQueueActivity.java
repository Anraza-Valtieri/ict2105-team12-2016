package com.example.chowdi.qremind.Vendor;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.rockerhieu.rvadapter.states.StatesRecyclerViewAdapter;

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
    private ValueEventListener listener;
    private View loadingView;
    private View emptyView;
    private View errorView;
    private StatesRecyclerViewAdapter statesRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.vendor_list_of_customers_in_queue_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv =(RecyclerView) findViewById(R.id.activity_vendor_customersInQueue_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));//layout to layout the items in RV
        rv.setHasFixedSize(true);
        adapter = new CustomerListAdapter();

        loadingView = getLayoutInflater().inflate(R.layout.view_loading, rv, false);
        errorView = getLayoutInflater().inflate(R.layout.view_error, rv, false);
        emptyView = getLayoutInflater().inflate(R.layout.empty_view, rv, false);
        statesRecyclerViewAdapter = new StatesRecyclerViewAdapter(adapter, loadingView, emptyView, errorView);

        rv.setAdapter(statesRecyclerViewAdapter);

        //rv.setAdapter(adapter);

        //initialize queue infos
        vendor = application.getVendorUser();

        if(vendor.getShops() != null){
            populateQueueInfoAdapter();
        }

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

    /**
     * To populate the queue with its information
     */
    private void populateQueueInfoAdapter() {
        //firebase = new Firebase(Constants.FIREBASE_SHOPS);
        firebase = new Firebase(Constants.FIREBASE_QUEUES);
        firebaseQueueRef = firebase.child(vendor.getShops().values().toArray()[0].toString());
        listener = firebaseQueueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear previous data.

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    QueueInfo queueInfo = ds.getValue(QueueInfo.class);
                    queueInfo.setQueue_key(ds.getKey());
                    Log.d("QUEUE KEY", "onCreate: "+queueInfo.getQueue_key());
                    adapter.addQueueInfo(queueInfo);
                }
                adapter.isQueueEmpty();
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
        public void isQueueEmpty(){
            if(queueInfoArrayList.isEmpty()){
                statesRecyclerViewAdapter.setState(StatesRecyclerViewAdapter.STATE_EMPTY);
            }else{
                statesRecyclerViewAdapter.setState(StatesRecyclerViewAdapter.STATE_NORMAL);
            }
        }
        public void removeQueueInfo(QueueInfo queueInfo){
            int position = queueInfoArrayList.indexOf(queueInfo);
            if(position == -1){//prevent double click
                return;
            }

            //remove firebase refs
            Firebase fbref = new Firebase(Constants.FIREBASE_CUSTOMER).child(queueInfo.getCustomer_id()).child("current_queue");
            fbref.removeValue();
            fbref = new Firebase(Constants.FIREBASE_SHOPS).child(vendor.getShops().values().toArray()[0].toString()).child("queues").child(queueInfo.getCustomer_id());
            fbref.removeValue();
            fbref = new Firebase(Constants.FIREBASE_QUEUES).child(vendor.getShops().values().toArray()[0].toString()).child(queueInfo.getQueue_key());
            fbref.removeValue();
            //
            queueInfoArrayList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
            adapter.isQueueEmpty();
        }

        //create view,layout
        @Override
        public CustomerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = getLayoutInflater().inflate(R.layout.vendor_list_customer_in_queue, parent, false);//layout inflater from the activity this class is in, the passed layout is given to the viewholder to inflate individual views
            return new CustomerListViewHolder(view);

        }

        //populate data
        @Override
        public void onBindViewHolder(CustomerListViewHolder holder, int position) {
            final QueueInfo qInfo = queueInfoArrayList.get(position);
            holder.itemView.setTag(qInfo);//tag each view to the DTO
            holder.queueNoTextView.setText(Integer.toString(qInfo.getQueue_no()));
            holder.inQueueDateTextView.setText(qInfo.getIn_queue_date());
            holder.inQueueTimeTextView.setText(qInfo.getIn_queue_time());

            //DQ button
            holder.dqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.removeQueueInfo(qInfo);
                }
            });
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
        public Button dqButton;
        public CustomerListViewHolder(View itemView) {
            super(itemView);
            queueNoTextView = (TextView)itemView.findViewById(R.id.list_item_customer_queueNo);
            inQueueDateTextView = (TextView)itemView.findViewById(R.id.list_item_customer_in_queue_date);
            inQueueTimeTextView = (TextView)itemView.findViewById(R.id.list_item_customer_in_queue_time);

            dqButton = (Button)itemView.findViewById(R.id.vendor_list_remove_customer_in_queue);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseQueueRef != null)
            firebaseQueueRef.removeEventListener(listener);
    }
}
