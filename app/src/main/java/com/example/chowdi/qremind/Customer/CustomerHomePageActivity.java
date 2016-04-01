package com.example.chowdi.qremind.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerHomePageActivity extends BaseActivity{
    //Variable for Firebase
    private Firebase firebase;
    private SharedPreferences prefs;

    // Variable for UI Elements
    //public static ArrayList categories;
    private Spinner spinnerCategory;
    private String userSelectCategory;
    private RecyclerView rv;

    // Other variables
    private ArrayList<Shop> shops;
    private ShopListAdapter adapter;
    private ArrayList<String> categories = new ArrayList<String>();
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerhomepage);
        setNavDrawer(new CustomerMainNavDrawer(this));
        shops = new ArrayList<>();

        // Initialise getSharedPreferences for this app and progress dialog
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);
        pd = new ProgressDialog(this);

        // Initialise all UI elements
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        rv = (RecyclerView)findViewById(R.id.activity_customerHomePage_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShopListAdapter();
        rv.setAdapter(adapter);


        Commons.showProgressDialog(pd, "Shop lists", "Loading shops info");
                /* Setting the Listener for the category spinner */
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
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
    /* Initializing the */
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

    private void populateShopListByCategory() {
        firebase = new Firebase(Constants.FIREBASE_SHOPS);

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear previous data.
                adapter.clearShops();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Shop shop = new Shop();
                    if (ds.child("category").getValue().toString().equals(userSelectCategory.toLowerCase().replaceAll(" ","_"))) {

                        shop.setShop_name(ds.child("shop_name").getValue().toString());
                        shop.setEmail(ds.child("email").getValue().toString());
                        shop.setQueueCount(ds.child("queues").getChildrenCount());
                        shop.setShop_key(ds.getKey());
                        adapter.addShop(shop);
                    }
                }
                Commons.dismissProgressDialog(pd);
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
            final View view = getLayoutInflater().inflate(R.layout.activity_customer_home_page_rv_list_item, parent, false);//layout inflater from the activity this class is in, the passed layout is given to the viewholder to inflate individual views
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Shop  shop = (Shop) view.getTag();
                    Toast.makeText(CustomerHomePageActivity.this,"Clicked on "+shop.getShop_name(),Toast.LENGTH_SHORT).show();
                }
            });
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
            holder.shopEmail.setText(shop.getEmail());
            holder.shopQueueCount.setText(Long.toString(shop.getQueueCount()));
            //button
            holder.qButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            notifyItemInserted(shops.size()-1);
        }

        public void clearShops(){
            shops.clear();
            notifyDataSetChanged();
        }
    }

    private class ShopViewHolder extends RecyclerView.ViewHolder{
        public TextView shopName;
        public TextView shopEmail;
        public TextView shopQueueCount;
        public Button qButton;
        public ShopViewHolder(View itemView) {
            super(itemView);
            shopName = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_shopName);
            shopEmail = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_shopEmail);
            shopQueueCount = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_inQueue);
            qButton = (Button)itemView.findViewById(R.id.activity_customerHomePage_list_item_joinQueue);
        }


    }

    public void getQueue(String shop_key,String customer_id,String shop_name){
        Commons.showProgressDialog(pd, "Please wait", "Getting queue number.");
        application.notificationSend = false;
        notificationPoppedOut = false;

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

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SHAREPREF_SHOP_NAME, shop_name);
        editor.putString(Constants.SHAREPREF_SHOP_KEY, shop_key);
        editor.putString(Constants.SHAREPREF_QUEUE_KEY, queueKey);
        editor.putString(Constants.SHAREPREF_QUEUE_NO, queueNo + "");
        editor.commit();

        Intent intent = new Intent(CustomerHomePageActivity.this, CustomerCurrentServing.class);
        startActivity(intent);
        Commons.dismissProgressDialog(pd);
        CustomerHomePageActivity.this.finish();
    }
}
