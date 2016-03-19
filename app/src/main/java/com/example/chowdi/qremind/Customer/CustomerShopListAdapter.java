package com.example.chowdi.qremind.Customer;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.models.Shop;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

/**
 * Created by L on 3/14/2016.
 */
public class CustomerShopListAdapter extends FirebaseListAdapter<Shop> {
    ProgressBar spinner;
    public CustomerShopListAdapter(Activity activity, Class<Shop> modelClass, int modelLayout, Firebase ref) {
        super(activity, modelClass, modelLayout, ref);
      //  spinner = (ProgressBar)activity.findViewById(R.id.progressBar1);

    }

    @Override
    protected void populateView(View view, Shop shop, int i) {
        ((TextView)view.findViewById(R.id.textViewShopName)).setText(shop.getShop_name());//id of a layout view
        spinner.setVisibility(View.GONE);
    }
}
