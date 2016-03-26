package com.example.chowdi.qremind;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chowdi.qremind.Customer.CustomerGetQueueActivity;
import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.models.Shop_Winnie;

import org.w3c.dom.Text;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ShopViewHolder> {

    public static class ShopViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView shopNameTV,categoryTV,phoneNumberTV,ratingsTV,emailTV;


        ShopViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            shopNameTV = (TextView)itemView.findViewById(R.id.shopnameTV);
            categoryTV = (TextView)itemView.findViewById(R.id.categoryTV);
            phoneNumberTV = (TextView)itemView.findViewById(R.id.phoneNumberTV);
            ratingsTV = (TextView) itemView.findViewById(R.id.ratingsTV);
            emailTV = (TextView) itemView.findViewById(R.id.emailTV);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Code that will redirect user to CustomerGetQueueActivity
                }
            });
        }
    }
    List<Shop_Winnie> shops;

    RVAdapter(List<Shop_Winnie> shops){
        this.shops = shops;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

//    @Override
//    public void onClick(View view) {
//        Log.d(TAG, "onClick"  + " " + );
//    }


    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_recycler_view_item, viewGroup, false);
        ShopViewHolder svh = new ShopViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(ShopViewHolder shopViewHolder, int i) {
        shopViewHolder.shopNameTV.setText(shops.get(i).name);
        shopViewHolder.categoryTV.setText(shops.get(i).category);
        shopViewHolder.phoneNumberTV.setText(shops.get(i).phoneNumber);
        shopViewHolder.ratingsTV.setText(shops.get(i).ratings);
        shopViewHolder.emailTV.setText(shops.get(i).email);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
}