package com.example.chowdi.qremind.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by anton on 9/3/2016.
 */
public class Commons {
    private static Toast toast = null;

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

        toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * To cancel and dismiss the current toast displayed
     */
    public static void cancelToastMessage()
    {
        if(toast != null)
            toast.cancel();
    }

    /**
     * Change the first letter of a string to uppercase
     * @param str
     */
    public static String firstLetterToUpper(String str)
    {
        if(str.length() <= 0 || str == null) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1, str.length());
    }

    /**
     * To show progress dialog
     * @param title title string
     * @param message message string
     */
    public static void showProgressDialog(ProgressDialog pd, String title, String message)
    {
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.show();
    }

    /**
     * To logout to main activity (Login_RegisterActivity)
     */
    public static void logout(Firebase fbRef, Activity currActivity)
    {
        fbRef.unauth();
        currActivity.finish();
    }

    /**
     * To handle all kind of firebase errors where to show the appropriate
     * and correct error messages on each errors
     * @param firebaseError FirebaseError
     */
    public static void handleCommonFirebaseError(FirebaseError firebaseError, Context context)
    {
        switch (firebaseError.getCode())
        {
            case FirebaseError.NETWORK_ERROR:
                Commons.showToastMessage("Network error!", context);
                break;
            case FirebaseError.DISCONNECTED:
                Commons.showToastMessage("Network disconnected!", context);
                break;
            case FirebaseError.INVALID_TOKEN:
                Commons.showToastMessage("Your session is expired", context);
                break;
            case FirebaseError.PERMISSION_DENIED:
                Commons.showToastMessage("permission denied", context);
                break;
            case FirebaseError.PROVIDER_ERROR:
                Commons.showToastMessage("provide error", context);
                break;
            case FirebaseError.LIMITS_EXCEEDED:
                Commons.showToastMessage("limits exceeded", context);
                break;
            case FirebaseError.OPERATION_FAILED:
                Commons.showToastMessage("operation failed", context);
                break;
            default:
                Commons.showToastMessage("Other errors", context);
                break;
        }
    }

    /**
     * To check if there is internet connections
     * @param context
     * @return true - internet connection available | false - no internet connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
