package com.example.chowdi.qremind.Vendor;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.infrastructure.QremindApplication;
import com.example.chowdi.qremind.infrastructure.QueueInfo;
import com.example.chowdi.qremind.infrastructure.Shop;
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
public class CardCurrentQueue extends Card{
    /**
     * Constructor with a custom inner layout
     * @param context
     */
    protected TextView mTitle;
    protected Button nextCustBtn;
    protected Button claimBtn;
    protected Button removeBtn;

    private Shop shop;
    private QueueInfo queueInfo;
    private QremindApplication application;

    public CardCurrentQueue(Context context, QremindApplication application) {
        this(context, R.layout.current_queue_card_vendor_dash_board);
        this.application = application;
        loadShopInfo();
    }

    /**
     *
     * @param context
     * @param innerLayout
     */
    public CardCurrentQueue(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    /**
     * Init
     */
    private void init(){

        //No Header

//        //Set a OnClickListener listener
//        setOnClickListener(new Card.OnCardClickListener() {
//            @Override
//            public void onClick(Card card, View view) {
//                Toast.makeText(getContext(), "Click Listener card=", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, View view) {
        mTitle = (TextView) parent.findViewById(R.id.card_extension_title);

        nextCustBtn = (Button)parent.findViewById(R.id.nextCustomerBtn);
        nextCustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickNextCustomer(v);
            }
        });

        claimBtn = (Button)parent.findViewById(R.id.vendor_curr_queue_claim_Btn);
        claimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, Void>(){
                    @Override
                    protected Void doInBackground(String... params) {
                        Intent intent = new Intent(getContext(), ClaimQRCodeActivity.class);
                        intent.putExtra(Constants.EX_MSG_QUEUE_INFO, queueInfo);
                        intent.putExtra(Constants.EX_MSG_SHOP_INFO, shop);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                        SystemClock.sleep(3000);
                        while (!ClaimQRCodeActivity.claimFinished) {SystemClock.sleep(1500);};

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (ClaimQRCodeActivity.claimCancelled)
                            return;
                        else
                        {
                            mTitle.setText("0");
                            nextCustBtn.setVisibility(View.VISIBLE);
                            claimBtn.setVisibility(View.INVISIBLE);
                            removeBtn.setVisibility(View.INVISIBLE);
                        }
                    }
                }.execute();
            }
        });

        removeBtn = (Button)parent.findViewById(R.id.vendor_curr_queue_removeBtn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase fbref = new Firebase(Constants.FIREBASE_CUSTOMER).child(queueInfo.getCustomer_id()).child("current_queue");
                fbref.removeValue();
                fbref = new Firebase(Constants.FIREBASE_SHOPS).child(shop.getShop_key()).child("queues").child(queueInfo.getCustomer_id());
                fbref.removeValue();
                fbref = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key()).child(queueInfo.getQueue_key());
                fbref.removeValue();
                nextCustBtn.setVisibility(View.VISIBLE);
                claimBtn.setVisibility(View.INVISIBLE);
                removeBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void OnClickNextCustomer(View v){
        callNextQueue();
    }

    protected void finishLoading(){

    }

    private void loadShopInfo()
    {
        final String shopKey = application.getVendorUser().getShops().values().toArray()[0].toString();
        Firebase fbRef = new Firebase(Constants.FIREBASE_SHOPS + "/" + shopKey);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shop = dataSnapshot.getValue(Shop.class);
                shop.setShop_key(shopKey);
                getNextQueueInfo();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getContext());
            }
        });
    }

    private void callNextQueue()
    {
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key());
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    DataSnapshot ds = null;
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        ds = d;
                        break;
                    }
                    Firebase fbref = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key()).child(ds.getKey());
                    fbref.child("calling").setValue(true);
                    getNextQueueInfo();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getContext());
            }
        });
    }

    private void getNextQueueInfo()
    {
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES + "/" + shop.getShop_key());
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    DataSnapshot ds = null;
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        ds = d;
                        break;
                    }

                    queueInfo = ds.getValue(QueueInfo.class);
                    queueInfo.setQueue_key(ds.getKey());
                    if(queueInfo.getCalling() != null) {
                        mTitle.setText(queueInfo.getQueue_no() + "");
                        nextCustBtn.setVisibility(View.INVISIBLE);
                        claimBtn.setVisibility(View.VISIBLE);
                        removeBtn.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                mTitle.setText("0");
                nextCustBtn.setVisibility(View.VISIBLE);
                claimBtn.setVisibility(View.INVISIBLE);
                removeBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getContext());
            }
        });
    }
}
