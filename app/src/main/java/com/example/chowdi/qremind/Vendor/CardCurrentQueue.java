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
    protected TextView currentQTV;
    protected TextView nextQTV;
    protected Button nextCustBtn;
    protected Button claimBtn;
    protected Button removeBtn;

    private Shop shopInfo;
    private QueueInfo queueInfo;
    private QremindApplication application;

    public CardCurrentQueue(Context context, QremindApplication application) {
        super(context, R.layout.vendor_card_current_queue);
        this.application = application;
        loadShopInfo();
    }

    @Override
    public void setupInnerViewElements(final ViewGroup parent, View view) {
        currentQTV = (TextView) parent.findViewById(R.id.currentQueueNo_TV);
        nextQTV = (TextView) parent.findViewById(R.id.nextQueueNo_TV);

        nextCustBtn = (Button)parent.findViewById(R.id.nextCustomerBtn);
        nextCustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if(!Commons.isNetworkAvailable(application))
                {
                    Commons.showToastMessage("No internet connection", application);
                    return;
                }
                callNextQueue();
            }
        });

        claimBtn = (Button)parent.findViewById(R.id.vendor_curr_queue_claim_Btn);
        claimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if (!Commons.isNetworkAvailable(application)) {
                    Commons.showToastMessage("No internet connection", application);
                    return;
                }
                showQRCode();
            }
        });

        removeBtn = (Button)parent.findViewById(R.id.vendor_curr_queue_removeBtn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if (!Commons.isNetworkAvailable(application)) {
                    Commons.showToastMessage("No internet connection", application);
                    return;
                }
                removeCalledQueue();
            }
        });
        nextQueueListener();
    }

    /**
     * Start another activity to show QR code for customer to claim queue
     */
    private void showQRCode()
    {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                Intent intent = new Intent(getContext(), ClaimQRCodeActivity.class);
                intent.putExtra(Constants.EX_MSG_QUEUE_INFO, queueInfo);
                intent.putExtra(Constants.EX_MSG_SHOP_INFO, shopInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
                SystemClock.sleep(1000);
                while (!ClaimQRCodeActivity.claimFinished) ;

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (ClaimQRCodeActivity.claimCancelled)
                    return;
                else {
                    currentQTV.setText("0000");
                    nextCustBtn.setVisibility(View.VISIBLE);
                    claimBtn.setVisibility(View.INVISIBLE);
                    removeBtn.setVisibility(View.INVISIBLE);
                }
            }
        }.execute();
    }

    /**
     * Load shop info from firebase and assign it to shopInfo variable
     */
    private void loadShopInfo()
    {
        final String shopKey = application.getVendorUser().getShops().values().toArray()[0].toString();
        Firebase fbRef = new Firebase(Constants.FIREBASE_SHOPS + "/" + shopKey);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shopInfo = dataSnapshot.getValue(Shop.class);
                shopInfo.setShop_key(shopKey);
                getNextQueueInfo();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError, getContext());
            }
        });
    }

    /**
     * Call next queue no
     */
    private void callNextQueue()
    {
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shopInfo.getShop_key());
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
                    Firebase fbref = new Firebase(Constants.FIREBASE_QUEUES).child(shopInfo.getShop_key()).child(ds.getKey());
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

    /**
     * Remove current called queue
     */
    private void removeCalledQueue()
    {
        Firebase fbref = new Firebase(Constants.FIREBASE_CUSTOMER).child(queueInfo.getCustomer_id()).child("current_queue");
        fbref.removeValue();
        fbref = new Firebase(Constants.FIREBASE_SHOPS).child(shopInfo.getShop_key()).child("queues").child(queueInfo.getCustomer_id());
        fbref.removeValue();
        fbref = new Firebase(Constants.FIREBASE_QUEUES).child(shopInfo.getShop_key()).child(queueInfo.getQueue_key());
        fbref.removeValue();
        currentQTV.setText("0000");
        nextCustBtn.setVisibility(View.VISIBLE);
        claimBtn.setVisibility(View.INVISIBLE);
        removeBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * Get next queue info and display on the UI.
     */
    private void getNextQueueInfo()
    {
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES + "/" + shopInfo.getShop_key());
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    DataSnapshot ds = null;
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        ds = d;
                        break;
                    }

                    queueInfo = ds.getValue(QueueInfo.class);
                    queueInfo.setQueue_key(ds.getKey());
                    if (queueInfo.getCalling() != null) {
                        currentQTV.setText(queueInfo.getQueue_no() + "");
                        nextCustBtn.setVisibility(View.INVISIBLE);
                        claimBtn.setVisibility(View.VISIBLE);
                        removeBtn.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                currentQTV.setText("0000");
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

    private void nextQueueListener()
    {
        final String shopKey = application.getVendorUser().getShops().values().toArray()[0].toString();
        Firebase fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey);
        fbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null)
                {
                    nextQTV.setText("----");
                    VendorDashBoardActivity.cardCurrentQueueLoaded = true;
                    return;
                }
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    QueueInfo queueInfo = ds.getValue(QueueInfo.class);
                    if(queueInfo.getCalling() != null)
                    {
                        nextQTV.setText("----");
                        continue;
                    }
                    nextQTV.setText(queueInfo.getQueue_no()+"");
                    VendorDashBoardActivity.cardCurrentQueueLoaded = true;
                    return;
                }
                VendorDashBoardActivity.cardCurrentQueueLoaded = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Commons.handleCommonFirebaseError(firebaseError,getContext());
            }
        });
    }
}