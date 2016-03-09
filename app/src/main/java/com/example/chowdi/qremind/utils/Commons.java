package com.example.chowdi.qremind.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * Created by anton on 9/3/2016.
 */
public class Commons {
    /**
     * Check if the value is an valid email
     * @param value email string
     * @return true for valid, false for invalid
     */
    public static Boolean isEmailString(String value) {
        String email_reg_exp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return value.matches(email_reg_exp);
    }

    /**
     * Check if the value is a number
     * @param value number string
     * @return true for valid, false for invalid
     */
    public static Boolean isNumberString(String value) {
        String number_reg_exp = "^[0-9]*$";
        return value.matches(number_reg_exp);
    }

    /**
     * Check if str is empty
     * @return true if empty, else false for non-empty
     */
    public static Boolean isEmptyString(String value) {
        return TextUtils.isEmpty(value);
    }

    /**
     * Show any messages on Toast
     * @param message - message string
     */
    public static void showToastMessage(String message, Context context)
    {
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    /**
     * To logout to main activity (Login_RegisterActivity)
     */
    public static void logout(Firebase fbRef, Activity currActivity)
    {
        fbRef.unauth();
        currActivity.finish();
    }

}
