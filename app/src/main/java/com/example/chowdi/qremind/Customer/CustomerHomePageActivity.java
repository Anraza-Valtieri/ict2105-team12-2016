package com.example.chowdi.qremind.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Shop;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.views.CustomerMainNavDrawer;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rockerhieu.rvadapter.states.StatesRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerHomePageActivity extends BaseActivity{
    //Variable for Firebase
    private Firebase firebase;

    // Variable for UI Elements
    private Spinner spinnerCategory;
    private String userSelectCategory;
    private RecyclerView rv;
    private View loadingView;
    private View emptyView;
    private View errorView;
    // Other variables
    private ArrayList<Shop> shops;
    private ShopListAdapter adapter;
    private StatesRecyclerViewAdapter statesRecyclerViewAdapter;
    private ArrayList<String> categories = new ArrayList<String>();
    private ProgressDialog pd;
    private AsyncTask runFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_homepage_activity);
        setNavDrawer(new CustomerMainNavDrawer(this));
        loadingView = getLayoutInflater().inflate(R.layout.view_loading, rv, false);
        errorView = getLayoutInflater().inflate(R.layout.view_error, rv, false);
        emptyView = getLayoutInflater().inflate(R.layout.view_empty, rv, false);
        shops = new ArrayList<>();

        // Initialise progress dialog
        pd = new ProgressDialog(this);

        // Initialise all UI elements
        initialiseUIElements();

        runFirst = new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute()
            {
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    ((RelativeLayout)findViewById(R.id.layout_no_connection)).setVisibility(View.VISIBLE);
                }
            }
            @Override
            protected Void doInBackground(Void... params)
            {
                // Check network connection
                while(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    SystemClock.sleep(1000);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                ((RelativeLayout)findViewById(R.id.layout_no_connection)).setVisibility(View.GONE);
                init();
            }
        }.execute();
    }
    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        rv = (RecyclerView)findViewById(R.id.activity_customerHomePage_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShopListAdapter();
        statesRecyclerViewAdapter = new StatesRecyclerViewAdapter(adapter, loadingView, emptyView, errorView);
        rv.setAdapter(statesRecyclerViewAdapter);
    }

    /**
     * Activity initialisation
     */
    private void init()
    {
        Commons.showProgressDialog(pd, "Shop lists", "Loading shops info");
        /* Setting the Listener for the category spinner */
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }

                userSelectCategory = String.valueOf(parent.getItemAtPosition(pos));

                /* Calls the populateShopListByCategory() function and populates the category spinner with data from Firebase */
                adapter.clearShops();
                populateShopListByCategory();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //Another Interface callback
            }
        });

        //init spinner data
        initializeCategory();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(runFirst.getStatus() == AsyncTask.Status.RUNNING || runFirst.getStatus() == AsyncTask.Status.PENDING)
        {
            runFirst.cancel(true);
        }
    }

    /**
     * Load and initialise category list
     */
    private void initializeCategory() {
        firebase = new Firebase(Constants.FIREBASE_CATEGORY);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    categories.add(ds.getValue().toString());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerHomePageActivity.this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                Commons.dismissProgressDialog(pd);
            }
        });

    }

    /**
     * Populate the shop list by selected category
     */
    private void populateShopListByCategory() {
        firebase = new Firebase(Constants.FIREBASE_SHOPS);

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear previous data.
                adapter.clearShops();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("category").getValue().toString().equals(userSelectCategory.toLowerCase().replaceAll(" ","_"))) {
                        Shop shop = ds.getValue(Shop.class);
                        shop.setShop_key(ds.getKey());
                        adapter.addShop(shop);
                    }
                }
                Commons.dismissProgressDialog(pd);
                adapter.isShopsEmpty();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                Commons.dismissProgressDialog(pd);
            }
        });
    }

    private class ShopListAdapter extends RecyclerView.Adapter<ShopViewHolder> {
        public ShopListAdapter(){

        }
        @Override
        public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //layout inflater from the activity this class is in, the passed layout is given to the viewholder to inflate individual views
            final View view = getLayoutInflater().inflate(R.layout.customer_home_page_rv_list_item, parent, false);
            view.setClickable(false);

            return new ShopViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return shops.size();
        }

        @Override
        public void onBindViewHolder(ShopViewHolder holder, int position) {
            final Shop shop = shops.get(position);
            holder.itemView.setTag(shop);
            holder.shopName.setText(shop.getShop_name());
            holder.shopLocation.setText(shop.getAddress());
            if(shop.getMyImage() != null) {
                holder.shopImage.setImageBitmap(shop.getMyImage());
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                    holder.shopImage.setImageDrawable(getDrawable(R.drawable.unknown_building));
                else
                    holder.shopImage.setImageDrawable((getResources().getDrawable(R.drawable.unknown_building)));
            }

            holder.shopQueueCount.setText(Long.toString(shop.getQueueCount()));

            //button
            holder.qButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Commons.isNetworkAvailable(getApplicationContext()))
                    {
                        Commons.showToastMessage("No internet connection", getApplicationContext());
                        return;
                    }
                    if(application.getCustomerUser().getCurrent_queue() == null){
                        getQueue(shop.getShop_key(),application.getCustomerUser().getPhoneno(),shop.getShop_name());
                    }else{
                        Toast.makeText(CustomerHomePageActivity.this,"You are already in queue",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        public void addShop(Shop shop){
            shops.add(shop);
            notifyItemInserted(shops.size() - 1);
        }
        public void isShopsEmpty(){
            if(shops.isEmpty()){
                statesRecyclerViewAdapter.setState(StatesRecyclerViewAdapter.STATE_EMPTY);
            }else{
                statesRecyclerViewAdapter.setState(StatesRecyclerViewAdapter.STATE_NORMAL);
            }
        }
        public void clearShops(){
            shops.clear();
            notifyDataSetChanged();
        }
    }

    private class ShopViewHolder extends RecyclerView.ViewHolder{
        public TextView shopName;
        public TextView shopLocation;
        public TextView shopQueueCount;
        public ImageView qButton;
        public ImageView shopImage;
        public ShopViewHolder(View itemView) {
            super(itemView);
            shopName = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_shopName);
            shopLocation = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_shopLocation);
            shopQueueCount = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_inQueue);
            qButton = (ImageView)itemView.findViewById(R.id.activity_customerHomePage_list_item_joinQueue);
            shopImage = (ImageView)itemView.findViewById(R.id.activity_customerHomePage_list_item_shopImage);
        }
    }

    /**
     * To get queue number for the selected shop
     * @param shop_key shop key node in Firebase
     * @param customer_id customer phone no
     * @param shop_name shop name for display
     */
    public void getQueue(String shop_key,String customer_id,String shop_name){
        Commons.showProgressDialog(pd, "Please wait", "Getting queue number.");
        // Set all notification to false first so that later the notification can be sent out when queue is reached
        application.notificationSend = false;
        notificationPoppedOut = false;

        /* Start queueing */
        Firebase vendorRef = new Firebase(Constants.FIREBASE_SHOPS + "/" + shop_key + "/queues");
        Firebase custRef = new Firebase(Constants.FIREBASE_CUSTOMER +"/"+ customer_id +"/current_queue");
        Firebase queueRef = new Firebase(Constants.FIREBASE_QUEUES +"/"+ shop_key).push();

        // map for queues
        Map<String, Object> qMap = new HashMap<String, Object>();
        qMap.put("customer_id", customer_id);
        qMap.put("connected", true);
        qMap.put("current_location", "nil");
        qMap.put("in_queue_date", new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        qMap.put("in_queue_time", new SimpleDateFormat("HH:mm").format(new Date()));
        qMap.put("time_ext_requested", false);
        queueRef.setValue(qMap); // insert into queues

        String queueKey = queueRef.getKey();
        int queueNo = Commons.keyToNoConverter(queueKey);
        queueRef.child("queue_no").setValue(queueNo); //update the queue no

        // map for customer current queue
        Map<String, Object> custMap = new HashMap<String, Object>();
        custMap.put("queue_key", queueKey);
        custMap.put("shop", shop_key);
        custRef.setValue(custMap); // insert into queues

        // map for customer current queue
        vendorRef.child(customer_id).setValue(true); // insert into queues
        /* Finish queueing */

        Intent intent = new Intent(CustomerHomePageActivity.this, CustomerCurrentServing.class);
        startActivity(intent);
        Commons.dismissProgressDialog(pd);
        CustomerHomePageActivity.this.finish();
    }
}
