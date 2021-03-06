package com.example.chowdi.qremind.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.Vendor.DashBoardActivity;
import com.example.chowdi.qremind.infrastructure.QremindApplication;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Contributed by Chin Zhi Qiang, Anton Salim on 3/14/2016.
 */
public class CardPeopleServed extends Card {
    /**
     * Constructor with a custom inner layout
     * @param context
     */
    protected TextView mTitle;
    protected TextView dateTV;

    private QremindApplication application;

    public CardPeopleServed(Context context, QremindApplication application) {
        super(context, R.layout.vendor_card_total_no_served_queue);
        this.application = application;
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, View view) {
        mTitle = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);
        dateTV = (TextView) parent.findViewById(R.id.servedqueue_datetime_TV);

        String today = new SimpleDateFormat("yyyy/M/d").format(new GregorianCalendar().getTime());
        dateTV.setText(today);

        final String shopKey = application.getVendorUser().getShops().values().toArray()[0].toString();
        GregorianCalendar datetime = new GregorianCalendar();
        String date = new SimpleDateFormat("yyyy/M/d").format(datetime.getTime());
        Firebase fbRef = new Firebase(Constants.FIREBASE_SERVED_QUEUES).child(shopKey).child(date);
        fbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                    mTitle.setText(dataSnapshot.getChildrenCount()+"");
                else
                    mTitle.setText("0");

                DashBoardActivity.cardPeopleServedLoaded = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getContext());
            }
        });
    }
}
