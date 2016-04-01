package com.example.chowdi.qremind.Vendor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.views.CustomerMainNavDrawer;

import java.util.ArrayList;

/**
 * Created by lotus on 4/1/2016.
 */
public class ListOfCustomersInQueueActivity extends BaseActivity{
    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.list_of_customers_in_queue_activity);
        setNavDrawer(new CustomerMainNavDrawer(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv =(RecyclerView) findViewById(R.id.activity_vendor_customersInQueue_recyclerView);

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

    //RV adapter private class and view holder
    private class CustomerListAdapter extends RecyclerView.Adapter<CustomerListViewHolder>{

        private final ArrayList<String> names;

        public CustomerListAdapter(){
            names = new ArrayList<>();
        }

        public void addName(String name){
            names.add(name);
            notifyItemInserted(names.size()-1);//insert at last

        }

        public void removeName(String name){
            int position = name.indexOf(name);
            if(position == -1){//prevent double click
                return;
            }
            names.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,names.size());
        }
        //create view,layout
        @Override
        public CustomerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = getLayoutInflater().inflate(R.layout.list_customer_in_queue, parent, false);//layout inflater from the activity this class is in, the passed layout is given to the viewholder to inflate individual views
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = (String) view.getTag();
                    removeName(name);
                }
            });

            return new CustomerListViewHolder(view);
        }

        //populate data
        @Override
        public void onBindViewHolder(CustomerListViewHolder holder, int position) {
            String name = names.get(position);
            holder.NameTextView.setText(name);
            holder.itemView.setTag(name);//tag each view to the DTO
            if(position % 2 == 0){//if even
                holder.NameTextView.setBackgroundColor(Color.parseColor("#22000000"));//alpha value first "22"
            }else{
                holder.NameTextView.setBackground(null);
            }
        }

        @Override
        public int getItemCount() {
            return names.size();
        }
    }

    private class CustomerListViewHolder extends RecyclerView.ViewHolder{
        public TextView NameTextView;

        public CustomerListViewHolder(View itemView) {
            super(itemView);
            NameTextView = (TextView)itemView.findViewById(R.id.list_item_customer_queueNo);
        }
    }
}
