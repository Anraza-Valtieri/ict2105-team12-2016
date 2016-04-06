package com.example.chowdi.qremind.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Contributed by Anton Salim on 24/3/2016.
 */
public class QRCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public static boolean scanningCancelled = false;
    public static boolean scanningFinished = false;
    public static String result = "";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);             // Set the scanner view as the content view
        scanningCancelled = false;
        scanningFinished = false;
        result = "";
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        scanningCancelled = true;
        scanningFinished = true;
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // If you would like to resume scanning, call this method below:
        if(!rawResult.getText().contains(Constants.CODE_QR_CLAIM_QUEUE))
        {
            // Continue scanning if invalid result
            Commons.showToastMessage("Invalid QR Code", this);
            mScannerView.resumeCameraPreview(this);
            return;
        }
        result = rawResult.getText().replace(Constants.CODE_QR_CLAIM_QUEUE,"");
        mScannerView.stopCamera();
        scanningFinished = true;
        scanningCancelled = false;
        this.finish();
    }
}