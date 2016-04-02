package com.example.chowdi.qremind.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Customer;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.utils.CustomisedTextWatcher;
import com.example.chowdi.qremind.views.CustomerMainNavDrawer;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomerProfilePageActivity extends BaseActivity{

    // Firebase variables
    Firebase fbRef;

    // Variables for all UI elements
    private EditText fName_ET, lName_ET, email_ET, phoneNo_ET;
    private Button updateBtn;
    private ImageView profilePic;

    // Other variables
    private ProgressDialog pd;
    private Customer customer;

    // Variables for Camera
    private static final int REQUEST_SELECT_IMAGE = 100;
    private File tempOutputFile;
    private Bitmap tempPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profilepage_activity);
        setNavDrawer(new CustomerMainNavDrawer(this));

        // Initialise all UI elements first and progress dialog
        initialiseUIElements();
        pd = new ProgressDialog(this);

        // Initialise Firebase setup
        fbRef = new Firebase(Constants.FIREBASE_CUSTOMER);

        // Get customer object from application
        customer = application.getCustomerUser();

        // Set CustomisedTextWatcher as TextChangeListener to all EditText
        fName_ET.addTextChangedListener(new CustomisedTextWatcher(fName_ET, (TextView) findViewById(R.id.custProfile_fName_TV), R.string.hint_fname));
        lName_ET.addTextChangedListener(new CustomisedTextWatcher(lName_ET, (TextView) findViewById(R.id.custProfile_lName_TV), R.string.hint_lname));
        email_ET.addTextChangedListener(new CustomisedTextWatcher(email_ET, (TextView) findViewById(R.id.custProfile_email_TV), R.string.hint_email));
        phoneNo_ET.addTextChangedListener(new CustomisedTextWatcher(phoneNo_ET, (TextView) findViewById(R.id.custProfile_phone_no_TV), R.string.hint_phone_no));

        // Check network connection
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            setEnableAllElements(false);
        }
        else
        {
            fName_ET.setText(customer.getFirstname());
            lName_ET.setText(customer.getLastname());
            email_ET.setText(customer.getEmail());
            phoneNo_ET.setText(customer.getPhoneno());
            if(customer.getMyImage() == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                    profilePic.setImageDrawable(getDrawable(R.drawable.unknown_person));
                else
                    profilePic.setImageDrawable(getResources().getDrawable(R.drawable.unknown_person));
            }
            else
                profilePic.setImageBitmap(customer.getMyImage());

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
                new AsyncTask<String, Void, Void>(){
                    @Override
                    protected Void doInBackground(String... params) {
                        // Wait for the camera starts up
                        fbRef.child(customer.getPhoneno()).child("firstname").setValue(params[0]);
                        fbRef.child(customer.getPhoneno()).child("lastname").setValue(params[1]);
                        if(tempPicture != null)
                            fbRef.child(customer.getPhoneno()).child("image").setValue(Commons.convertBitmapToBase64(tempPicture));
                        SystemClock.sleep(1000);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Commons.dismissProgressDialog(pd);
                        Commons.showToastMessage("Profile updated!", getApplicationContext());
                        setEnableAllElements(true);
                        navDrawer.UpdateNavbarView();
                    }
                }.execute(fName_ET.getText().toString(), lName_ET.getText().toString());
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
        // Convert image to tempPicture
        // Set tempPicture to Customer's profile picture
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
            tempPicture = BitmapFactory.decodeFile(tempOutputFile.getPath());
            profilePic.setImageBitmap(tempPicture);
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
