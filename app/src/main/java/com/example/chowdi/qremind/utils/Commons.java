package com.example.chowdi.qremind.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chowdi.qremind.Customer.CustomerProfilePageActivity;
import com.example.chowdi.qremind.Login_RegisterActivity;
import com.example.chowdi.qremind.R;
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
        int duration = Toast.LENGTH_LONG;

        cancelToastMessage(); // Cancel any current toast is being displayed

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
     * To dismiss progress dialog
     */
    public static void dismissProgressDialog(ProgressDialog pd)
    {
        try{
            pd.dismiss();
        }catch (Exception ex)
        {
            Log.d("Exception Msg", ex.getMessage());
        }
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

    /**
     * Converter a string key to a number int
     * @param key key string
     * @return converted key number
     */
    public static int keyToNoConverter(String key)
    {
        int number = 0;
        for(char c : key.toCharArray()){
            number += c;
        }
        return number;
    }

    /**
     * To add clickable items for current context side navigation menu into an adapter and set this adapter to mDrawerList's adapter.
     * @param currContext current context
     * @param mDrawerList a listview in the current context
     */
    public static void addDrawerItems(final Activity currContext, ListView mDrawerList) {
        String[] navSidebar = { "Profile", "Logout"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(currContext, android.R.layout.simple_list_item_1, navSidebar);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(currContext, CustomerProfilePageActivity.class);
                    currContext.startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(currContext, Login_RegisterActivity.class);
                    currContext.startActivity(intent);
                }
            }
        });
    }

    /**
     * To set and attach drawer to current context as side navigation menu
     * @param currContext current context
     * @param mDrawerLayout the layout for the drawer that is created for the current context
     * @return ActionBarDrawerToggle will be used for override method in current context
     */
    public static ActionBarDrawerToggle setupDrawer(final Activity currContext, DrawerLayout mDrawerLayout) {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(currContext, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                currContext.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                currContext.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        return mDrawerToggle;
    }
}
