package com.example.chowdi.qremind.Vendor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.QueueInfo;
import com.example.chowdi.qremind.infrastructure.Shop;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.WriterException;

/**
 * Created by anton on 27/3/2016.
 */
public class ClaimQRCodeActivity extends BaseActivity {

    // Firebase variables
    private Firebase fbRef;
    private ValueEventListener listener;

    // Variables for all UI elements
    private ImageView qrCodeImage;
    private TextView shopNameTV;
    private TextView queueNoTV;
    private TextView phoneNoTV;

    // Other variables
    public static Boolean claimFinished;
    public static Boolean claimCancelled;
    private Shop shop;
    private QueueInfo queueInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_claim_qr_code);
        claimFinished = false;
        claimCancelled = false;

        // Initialise all UI elements first
        initialiseUIElements();

        try {
            queueInfo = getIntent().getParcelableExtra(Constants.EX_MSG_QUEUE_INFO);
            shop = getIntent().getParcelableExtra(Constants.EX_MSG_SHOP_INFO);

            shopNameTV.setText(shop.getShop_name());
            queueNoTV.setText(getString(R.string.text_queue_no) + ": " + queueInfo.getQueue_no());
            phoneNoTV.setText(getString(R.string.text_phone_no) + ": " + queueInfo.getCustomer_id());

            Bitmap bitmap = Commons.generateQRCodeImage(Constants.CODE_QR_CLAIM_QUEUE +queueInfo.getQueue_key());
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        fbRef = new Firebase(Constants.FIREBASE_QUEUES).child(shop.getShop_key()).child(queueInfo.getQueue_key());
        listener = fbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    claimFinished = true;
                    fbRef.removeEventListener(listener);
                    ClaimQRCodeActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        qrCodeImage = (ImageView) findViewById(R.id.vendor_qr_code_ImageView);
        shopNameTV = (TextView) findViewById(R.id.vendor_qr_claim_shop_name_TV);
        queueNoTV = (TextView)findViewById(R.id.vendor_qr_claim_queue_no_TV);
        phoneNoTV = (TextView)findViewById(R.id.vendor_qr_claim_phone_no_TV);
    }

    @Override
    public void onBackPressed() {
        if(fbRef != null)
            fbRef.removeEventListener(listener);
        this.finish();
        claimFinished = true;
        claimCancelled = true;
    }
}
