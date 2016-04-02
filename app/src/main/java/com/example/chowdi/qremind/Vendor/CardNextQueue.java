package com.example.chowdi.qremind.Vendor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.infrastructure.QremindApplication;
import com.example.chowdi.qremind.infrastructure.QueueInfo;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by L on 3/14/2016.
 */
public class CardNextQueue extends Card {
    /**
     * Constructor with a custom inner layout
     * @param context
     */
    protected TextView mTitle;

    private QremindApplication application;

    public CardNextQueue(Context context, QremindApplication application) {
        super(context, R.layout.vendor_next_queue_card_dash_board);
        this.application = application;
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, View view) {
        mTitle = (TextView) parent.findViewById(R.id.card_extension_title);

        final String shopKey = application.getVendorUser().getShops().values().toArray()[0].toString();
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey);
        fbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null)
                {
                    mTitle.setText("No Queue");
                    return;
                }
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    QueueInfo queueInfo = ds.getValue(QueueInfo.class);
                    if(queueInfo.getCalling() != null)
                    {
                        mTitle.setText("No Queue");
                        continue;
                    }
                    mTitle.setText(queueInfo.getQueue_no()+"");
                    return;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError,getContext());
            }
        });
    }
}
