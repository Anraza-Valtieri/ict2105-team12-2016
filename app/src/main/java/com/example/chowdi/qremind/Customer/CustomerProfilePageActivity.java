package com.example.chowdi.qremind.Customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

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

        // Open camera app and take picture to update customer's profile picture
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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
        super.onActivityResult(requestCode, resultCode, data);

        // Allow user to choose whether customer wants to update his profile picture with camera or
        // choose from gallery
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    /**
     * To open a new window within app allowing user to choose the different options to set
     * his profile picture
     * @param
     */
    private void selectImage(){
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerProfilePageActivity.this);
        builder.setTitle("Change Profile Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // Open camera app, take picture and update customer's profile picture
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profilePic.setImageBitmap(thumbnail);
    }

    // Choose photo from gallery and set it as customer's profile picture
    private void onSelectFromGalleryResult(Intent data) {
        try
        {
            Commons.uri = data.getData();
            if (Commons.uri != null)
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Commons.uri);
                profilePic.setImageBitmap(bitmap);
            } else
            {
                Toast toast = Toast.makeText(this, "Sorry!!! You haven't select any image.", Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (Exception e)
        {
            // you get this when you did not select any single image
            Log.e("onActivityResult", "" + e);

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
