package com.example.chowdi.qremind.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.chowdi.qremind.MainActivity;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

/**
 * Contributed by Anton Salim on 9/3/2016.
 */
public class Commons {
    private static Toast toast = null;
    public static Uri uri;

    // For Generating QR Code
    private static int WIDTH = 500;
    private static int BLACK = 0xFF000000;
    private static int WHITE = 0xFFFFFFFF;

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
     * @param pd progress dialog used in the current activity
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
     * @param fbRef firebase to this application context
     * @param currActivity the current activity to be closed
     */
    public static void logout(Firebase fbRef, BaseActivity currActivity)
    {
        fbRef.unauth();
        SharedPreferences prefs = currActivity.getSharedPreferences(Constants.SHARE_PREF_LINK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.SHAREPREF_EMAIL);
        editor.remove(Constants.SHAREPREF_PHONE_NO);
        editor.remove(Constants.SHAREPREF_ROLE);
        editor.remove(Constants.SHAREPREF_VENDOR_SHOP_KEY);
        editor.commit();
        currActivity.getQremindApplication().releaseCustomerUser();
        currActivity.getQremindApplication().releaseVendorUser();
        Intent intent = new Intent(currActivity, MainActivity.class);
        currActivity.startActivity(intent);
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
     * To generate a QR code image
     * @param str string of url or any text to be converted to QR code
     * @return QR code image
     * @throws WriterException
     */
    public static Bitmap generateQRCodeImage(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * Convert Base64 back to Bitmap
     * @param imageString base64 string
     */
    public static Bitmap convertBase64ToBitmap(String imageString)
    {
        try {
            byte[] imageAsBytes = Base64.decode(imageString.getBytes(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    /**
     * Convert bitmap image to Base64
     * @param image any valid Bitmap image
     * @return Base64 image string
     */
    public static String convertBitmapToBase64(Bitmap image)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

}
