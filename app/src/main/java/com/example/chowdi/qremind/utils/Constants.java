package com.example.chowdi.qremind.utils;

/**
 * Created by L on 3/6/2016.
 */
public final class Constants {

    //prevent instantiation
    private Constants(){

    }

    // For Firebase
    public static final String  FIREBASE_MAIN = "https://qremind1.firebaseio.com";
    public static final String  FIREBASE_CUSTOMER = "https://qremind1.firebaseio.com/AppUserAccount/Customer";
    public static final String  FIREBASE_VENDOR = "https://qremind1.firebaseio.com/AppUserAccount/Vendor";

    // For shared preferences
    public static final String SHARE_PREF_LINK = "QREMIND_SP";
    public static final String SHAREPREF_EMAIL = "EMAIL";
    public static final String SHAREPREF_PHONE_NO = "PHONE_NO";
    public static final String SHAREPREF_ROLE = "ROLE";

    // For others
    public static final String ROLE_VENDOR = "VENDOR";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
}
