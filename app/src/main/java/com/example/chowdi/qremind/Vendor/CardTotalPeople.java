package com.example.chowdi.qremind.Vendor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by L on 3/13/2016.
 */
public class CardTotalPeople extends Card {

    /**
     * Constructor with a custom inner layout
     * @param context
     */
    protected TextView mTitle;
    private String shopName ="Happy Bowling Centre";
    public CardTotalPeople(Context context) {
        this(context, R.layout.card_vendor_dash_board);
    }

    /**
     *
     * @param context
     * @param innerLayout
     */
    public CardTotalPeople(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    /**
     * Init
     */
    private void init(){

        //No Header

        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getContext(), "Click Listener card=", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, View view) {
        //Firebase ref
        Firebase ref = new Firebase(Constants.FIREBASE_SHOPS+"/"+shopName+"/queue");

        mTitle = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);

        if (mTitle!=null)
        {

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    mTitle.setText("" + snapshot.getChildrenCount());


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

        }

    }

    protected void finishLoading(){

    }
}
