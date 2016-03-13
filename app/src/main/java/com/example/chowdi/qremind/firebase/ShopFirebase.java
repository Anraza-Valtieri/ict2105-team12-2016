package com.example.chowdi.qremind.firebase;

import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;

/**
 * Created by L on 3/6/2016.
 */
public class ShopFirebase {
    Firebase refBase;
    private static final String TAG = ShopFirebase.class.getSimpleName();

    public ShopFirebase(){
        refBase = new Firebase(Constants.VENDOR_QUEUES);
    }


}
