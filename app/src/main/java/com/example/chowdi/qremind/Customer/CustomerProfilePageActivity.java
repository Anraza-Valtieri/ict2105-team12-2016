package com.example.chowdi.qremind.Customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.views.CustomerMainNavDrawer;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerProfilePageActivity extends BaseActivity{

    // Firebase variables
    Firebase fbRef;

    // Variables for all UI elements
    private EditText fName_ET, lName_ET, email_ET, phoneNo_ET;
    private Button updateBtn, logoutBtn;
    private ImageView profilePic;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    // Other variables
    private SharedPreferences prefs;
    private String phoneNo;
    private ProgressDialog pd;

    // Variables for Camera
    private static final int REQUEST_SELECT_IMAGE = 100;
    private File tempOutputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerprofilepage);
        setNavDrawer(new CustomerMainNavDrawer(this));
        // Initialise Firebase library with android context once before any Firebase reference is created or used
        Firebase.setAndroidContext(getApplicationContext());

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Initialise getSharedPreferences for this app and Firebase setup
        prefs = getSharedPreferences(Constants.SHARE_PREF_LINK,MODE_PRIVATE);
        fbRef = new Firebase(Constants.FIREBASE_CUSTOMER);


        // Retrieve phone no from share preference to retrieve user information and display on the view
        phoneNo = prefs.getString(Constants.SHAREPREF_PHONE_NO, null);

        // Check network connection
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            setEnableAllElements(false);
        }
        else
        {
            Commons.showProgressDialog(pd, "Profile info", "Loading profile information");
            fbRef.child(phoneNo).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fName_ET.setText(dataSnapshot.child("firstname").getValue().toString());
                    lName_ET.setText(dataSnapshot.child("lastname").getValue().toString());
                    email_ET.setText(dataSnapshot.child("email").getValue().toString());
                    phoneNo_ET.setText(dataSnapshot.child("phoneno").getValue().toString());
                    Commons.dismissProgressDialog(pd);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    handleFirebaseError(firebaseError);
                }
            });
        }

        // Set and implement listener to update button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }
                setEnableAllElements(false);

                // If session expired, close current activity and go to login activity
                if(fbRef.getAuth() == null)
                {
                    Commons.logout(fbRef, CustomerProfilePageActivity.this);
                    Commons.showToastMessage("Your session expired!", getApplicationContext());
                    return;
                }

                Commons.showProgressDialog(pd, "Profile info", "Loading profile information");
                fbRef.child(phoneNo).child("firstname").setValue(fName_ET.getText().toString());
                fbRef.child(phoneNo).child("lastname").setValue(lName_ET.getText().toString());
                Commons.showToastMessage("Profile updated!", getApplicationContext());
                setEnableAllElements(true);
                Commons.dismissProgressDialog(pd);
            }
        });

        // Call changeAvatar() function which will open a small window allowing user to choose
        // which application to use to update his profile picture
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
            }
        });

        // For temporary only
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.logout(fbRef, CustomerProfilePageActivity.this);
            }
        });
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        fName_ET = (EditText) findViewById(R.id.custProfile_fName_ET);
        lName_ET = (EditText) findViewById(R.id.custProfile_lName_ET);
        email_ET = (EditText) findViewById(R.id.custProfile_email_ET);
        phoneNo_ET = (EditText) findViewById(R.id.custProfile_phone_no_ET);
        profilePic = (ImageView) findViewById(R.id.custProfile_picture);
        updateBtn = (Button) findViewById(R.id.custProfile_udpatebtn);
        logoutBtn = (Button) findViewById(R.id.custProfile_logoutbtn);
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        tempOutputFile = new File(getExternalCacheDir(), "temp-image.jpg");
    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        fName_ET.setEnabled(value);
        lName_ET.setEnabled(value);
        email_ET.setEnabled(value);
        phoneNo_ET.setEnabled(value);
        updateBtn.setEnabled(value);
        logoutBtn.setEnabled(value);
        mDrawerList.setEnabled(value);
        mDrawerLayout.setEnabled(value);
    }

    /**
     * To handle all kind of firebase errors where to show the appropriate
     * and correct error messages on each errors
     * @param firebaseError FirebaseError
     */
    private void handleFirebaseError(FirebaseError firebaseError)
    {
        switch (firebaseError.getCode())
        {
            default:
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                break;
        }
        setEnableAllElements(true);
        Commons.dismissProgressDialog(pd);
    }

    /**
     * Allow user to choose different options to change his/her profile picture:
     * 1) Change profile picture through android default camera app
     * 2) Change profile picture from existing pictures in gallery
     * @param
     */
    private void changeAvatar() {
        List<Intent> otherImageCaptureIntents = new ArrayList<>();
        List<ResolveInfo> otherImageCaptureActivities = getPackageManager()
                .queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);

        for (ResolveInfo info : otherImageCaptureActivities) {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempOutputFile));
            otherImageCaptureIntents.add(captureIntent);
        }

        Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
        selectImageIntent.setType("image/*");

        Intent chooser = Intent.createChooser(selectImageIntent, "Chooser Avatar");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, otherImageCaptureIntents.toArray(new Parcelable[otherImageCaptureActivities.size()]));

        startActivityForResult(chooser, REQUEST_SELECT_IMAGE);
    }

    /**
     * To set customer's profile image based on the requestCode passed to startActivityForResult(),
     * resultCode whether it passed or failed and the result data the intent is carrying
     * @param requestCode - Helps you to identify from which Intent you came back
     * @param resultCode - This is either RESULT_OK if the operation was successful or
     *                     RESULT_CANCELED if the user backed out or the operation
     *                     failed for some reason.
     * @param data - Result data Intent is carrying
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK) {
            tempOutputFile.delete();
            return;
        }

        // Retrieve image file path
        // Crop image
        // Convert image to bitmap
        // Set bitmap to Customer's profile picture
        if (requestCode == REQUEST_SELECT_IMAGE) {
            Uri outputFile;
            Uri tempFileUri = Uri.fromFile(tempOutputFile);

            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)))
                outputFile = data.getData();
            else
                outputFile = tempFileUri;

            new Crop(outputFile)
                    .asSquare()
                    .output(tempFileUri)
                    .start(this);
        } else if (requestCode == Crop.REQUEST_CROP) {
            Bitmap bitmap = BitmapFactory.decodeFile(tempOutputFile.getPath());
            profilePic.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // To cancel and dismiss all current toast
        Commons.cancelToastMessage();
    }

}
