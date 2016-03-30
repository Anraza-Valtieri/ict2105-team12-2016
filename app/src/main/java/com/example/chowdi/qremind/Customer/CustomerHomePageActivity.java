package com.example.chowdi.qremind.Customer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Shop;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.views.CustomerMainNavDrawer;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class CustomerHomePageActivity extends BaseActivity{
    //Variable for Firebase
    private Firebase firebase;
    private SharedPreferences sharedPreferences;

    // Variable for UI Elements
    //public static ArrayList categories;
    private ArrayList<String> categories = new ArrayList<String>();
    private String[] ratings = {"1","2", "3","4","5"};
    private Spinner spinnerCategory, spinnerRatings;
    private String userSelectCategory,userSelectRatings, shopName,phoneNumber,email,ratingsOfShop,categoryOfShop;
    private RecyclerView rv;
    private ArrayList<Shop> shops;
    private ListView listView;
    private TextView shopNameTV,categoryTV,phoneNumberTV;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ShopListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerhomepage);
        setNavDrawer(new CustomerMainNavDrawer(this));
        shops = new ArrayList<>();
        // Initialise all UI elements
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        spinnerRatings = (Spinner) findViewById((R.id.spinner_ratings));
        rv = (RecyclerView)findViewById(R.id.activity_customerHomePage_recyclerView);
        adapter = new ShopListAdapter();
        rv.setAdapter(adapter);
        //init spinner data
        initializeCategory();
        //initializeRating();

        /* Setting the Listener for the category spinner */
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                userSelectCategory = String.valueOf(parent.getItemAtPosition(pos));

                /* Calls the populateShopListByCategory() function and populates the category spinner with data from Firebase */
                populateShopListByCategory();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Another Interface callback
            }
        });

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

            }
        });

    }

//    private void populateShopListByRating() {
//        firebase = new Firebase("https://qremind1.firebaseio.com/shop_test");
//        firebase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Shop shop = new Shop();
//                    if (ds.child("ratings").getValue().toString().equals(userSelectRatings)) {
//                        shop.setShop_name(ds.child("shop_name").getValue().toString());
//                        shop.setEmail(email = ds.child("email").getValue().toString());
//                        adapter.addShop(shop);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//            }
//        });
//    }

    private void populateShopListByCategory() {
        //firebase = new Firebase(Constants.FIREBASE_SHOPS);
        firebase = new Firebase("https://qremind1.firebaseio.com/shop_test");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear previous data.
                adapter.clearShops();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Shop shop = new Shop();
                    if (ds.child("category").getValue().toString().equals(userSelectCategory)) {
                        shop.setShop_name(ds.child("shop_name").getValue().toString());
                        shop.setEmail(email = ds.child("email").getValue().toString());
                        adapter.addShop(shop);
                    }
                    // Log.d("pass short_test_1", "if loop failed/");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private class ShopListAdapter extends RecyclerView.Adapter<ShopViewHolder> {


        public ShopListAdapter(){
            shops.add(new Shop("Shop","test shop"));
        }

        @Override
        public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = getLayoutInflater().inflate(R.layout.activity_customer_home_page_rv_list_item, parent, false);//layout inflater from the activity this class is in, the passed layout is given to the viewholder to inflate individual views
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String name = (String) view.getTag();
//                    removeName(name);
//                }
//            });
            return new ShopViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return shops.size();
        }

        @Override
        public void onBindViewHolder(ShopViewHolder holder, int position) {
            Shop shop = shops.get(position);
            holder.shopName.setText(shop.getShop_name());
            holder.shopEmail.setText(shop.getEmail());

            //holder.itemView.setTag(name);//tag each view to the DTO
//            if(position % 2 == 0){//if even
//                holder.NameTextView.setBackgroundColor(Color.parseColor("#22000000"));//alpha value first "22"
//            }else{
//                holder.NameTextView.setBackground(null);
//            }
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

    private class ShopViewHolder extends RecyclerView.ViewHolder {
        public TextView shopName;
        public TextView shopEmail;

        public ShopViewHolder(View itemView) {
            super(itemView);
            shopName = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_shopName);
            shopEmail = (TextView)itemView.findViewById(R.id.activity_customerHomePage_list_item_shopEmail);

        }
    }
}
