package com.example.chowdi.qremind.utils;

/**
 * Contributed by Chin Zhi Qiang, Anton Salim, Winnie Lew, Edmund Chow on 3/6/2016.
 */
public final class Constants {

    //prevent instantiation
    private Constants(){

    }

    // For Firebase
    public static final String FIREBASE_MAIN = "https://qremindapp.firebaseio.com";
    public static final String FIREBASE_CUSTOMER = "https://qremindapp.firebaseio.com/appUserAccount/customer";
    public static final String FIREBASE_VENDOR = "https://qremindapp.firebaseio.com/appUserAccount/vendor";
    public static final String FIREBASE_CATEGORY = "https://qremindapp.firebaseio.com/category";
    public static final String FIREBASE_SHOPS = "https://qremindapp.firebaseio.com/shops";
    public static final String FIREBASE_QUEUES = "https://qremindapp.firebaseio.com/queues";
    public static final String FIREBASE_SERVED_QUEUES = "https://qremindapp.firebaseio.com/served_queues";

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
