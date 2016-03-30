package com.example.chowdi.qremind.Vendor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.views.VendorMainNavDrawer;
import com.google.zxing.WriterException;

/**
 * Created by anton on 27/3/2016.
 */
public class ClaimQRCodeActivity extends BaseActivity {

    // Variables for all UI elements
    ImageView qrCodeImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_claim_qr_code);
        setNavDrawer(new VendorMainNavDrawer(this));

        // Initialise all UI elements first
        initialiseUIElements();

        try {
            Bitmap bitmap = Commons.generateQRCodeImage("12345");
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        qrCodeImage = (ImageView) findViewById(R.id.vendor_qr_code_ImageView);
    }
}
