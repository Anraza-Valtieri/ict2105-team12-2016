package com.example.chowdi.qremind.utils;

/**
 * Created by L on 3/6/2016.
 */
public final class Constants {

    //prevent instantiation
    private Constants(){

    }

    // For Firebase
    public static final String FIREBASE_MAIN = "https://qremind1.firebaseio.com";
    public static final String FIREBASE_CUSTOMER = "https://qremind1.firebaseio.com/appUserAccount/customer";
    public static final String FIREBASE_VENDOR = "https://qremind1.firebaseio.com/appUserAccount/vendor";
    public static final String FIREBASE_CATEGORY = "https://qremind1.firebaseio.com/category";
    public static final String FIREBASE_SHOPS = "https://qremind1.firebaseio.com/shops";
    public static final String FIREBASE_QUEUES = "https://qremind1.firebaseio.com/queues";
    public static final String FIREBASE_SERVED_QUEUES = "https://qremind1.firebaseio.com/served_queues";

    // For shared preferences
    public static final String SHARE_PREF_LINK = "QREMIND_SP";
    public static final String SHAREPREF_EMAIL = "EMAIL";
    public static final String SHAREPREF_PHONE_NO = "PHONE_NO";
    public static final String SHAREPREF_ROLE = "ROLE";
    public static final String SHAREPREF_VENDOR_SHOP_KEY = "SHOP_KEY";

    // For extra messages
    public static final String EX_MSG_QUEUE_INFO = "QUEUE_INFO";
    public static final String EX_MSG_SHOP_INFO = "SHOP_INFO";

    // For QR code content
    public static final String CODE_QR_CLAIM_QUEUE = "QRemind_Claim_Queue:";

    // For others
    public static final String ROLE_VENDOR = "VENDOR";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

}
