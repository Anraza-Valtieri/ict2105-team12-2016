package com.example.chowdi.qremind.Vendor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Vendor;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.utils.CustomisedTextWatcher;
import com.example.chowdi.qremind.views.VendorMainNavDrawer;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessProfileActivity extends BaseActivity{
    // Firebase variables
    Firebase fbRef;

    // Variables for all UI elements
    private EditText shopName_ET;
    private EditText location_ET;
    private EditText email_ET;
    private EditText telephone_ET;
    private Spinner category_Spinner;
    private Button createBtn;
    private Button updateBtn;
    private ImageView profilePic;

    // Other variables
    private ArrayAdapter<String> adapter;
    private ProgressDialog pd;
    private Vendor user;

    // Variables for Camera
    private static final int REQUEST_SELECT_IMAGE = 100;
    private File tempOutputFile;
    private Bitmap tempPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_profilepage_activity);
        setNavDrawer(new VendorMainNavDrawer(this));

        // Initialise all UI elements first
        initialiseUIElements();

        // Initialise progress dialog, getSharedPreferences for this app, and firebase
        pd = new ProgressDialog(this);
        user = application.getVendorUser();
        fbRef = new Firebase(Constants.FIREBASE_MAIN);

        // addTextChangeListener to all EditTexts
        email_ET.addTextChangedListener(new CustomisedTextWatcher(email_ET, (TextView) findViewById(R.id.businessProf_email_TV), R.string.hint_vendor_email));
        shopName_ET.addTextChangedListener(new CustomisedTextWatcher(shopName_ET, (TextView) findViewById(R.id.businessProf_shopName_TV), -1));
        location_ET.addTextChangedListener(new CustomisedTextWatcher(location_ET, (TextView) findViewById(R.id.businessProf_location_TV), R.string.hint_vendor_location));
        telephone_ET.addTextChangedListener(new CustomisedTextWatcher(telephone_ET, (TextView) findViewById(R.id.businessProf_telephone_TV), R.string.hint_vendor_number));

        // set click listener to udpate and create button
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }
                if(!Commons.isEmailString(email_ET.getText().toString()))
                {
                    Commons.showToastMessage("Please provide valid email!", getApplicationContext());
                    email_ET.setError("Please provide valid email!");
                    return;
                }
                email_ET.setError(null);
                createShop();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connection
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }
                if(!Commons.isEmailString(email_ET.getText().toString()))
                {
                    Commons.showToastMessage("Please provide valid email!", getApplicationContext());
                    email_ET.setError("Please provide valid email!");
                    return;
                }
                email_ET.setError(null);
                updateShopInfo();
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

        setEnableAllElements(false);
        loadCategoryList();
    }

    /**
     * Find and assign the correct UI elements to the correct variables from activity_register layout
     */
    private void initialiseUIElements()
    {
        shopName_ET = (EditText) findViewById(R.id.businessProf_shopName_ET);
        location_ET = (EditText) findViewById(R.id.businessProf_location_ET);
        email_ET = (EditText) findViewById(R.id.businessProf_email_ET);
        telephone_ET = (EditText) findViewById(R.id.businessProf_telephone_ET);
        category_Spinner = (Spinner) findViewById(R.id.businessProf_category_Spinner);
        createBtn = (Button) findViewById(R.id.businessProf_createbtn);
        updateBtn = (Button) findViewById(R.id.businessProf_updatebtn);
        profilePic = (ImageView) findViewById(R.id.businessProf_picture);
        tempOutputFile = new File(getExternalCacheDir(), "temp-image.jpg");

    }

    /**
     * To set all UI elements in the view to be enabled or disabled
     * @param value true or false to enable or disable respectively
     */
    private void setEnableAllElements(boolean value)
    {
        shopName_ET.setEnabled(value);
        location_ET.setEnabled(value);
        email_ET.setEnabled(value);
        telephone_ET.setEnabled(value);
        category_Spinner.setEnabled(value);
        createBtn.setEnabled(value);
        updateBtn.setEnabled(value);
    }

    /**
     * To create new shop
     */
    private void createShop()
    {
        Commons.showProgressDialog(pd, "Shop info", "Creating shop");
        final String shopname = shopName_ET.getText().toString();
        final String location = location_ET.getText().toString();
        final String email = email_ET.getText().toString();
        final String phoneno = telephone_ET.getText().toString();
        final String category = category_Spinner.getSelectedItem().toString().toLowerCase();

        fbRef = new Firebase(Constants.FIREBASE_SHOPS);
        fbRef.child(shopname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Commons.dismissProgressDialog(pd);
                if (dataSnapshot.getValue() != null) {
                    Commons.showToastMessage("Shop name is already taken!", getApplicationContext());
                    shopName_ET.setError("Shop name is already taken!");
                    setEnableAllElements(true);
                } else {
                    fbRef = new Firebase(Constants.FIREBASE_VENDOR);
                    String shopkey = shopname.replaceAll(" ", "_").toLowerCase();
                    String vendorkey = application.getVendorUser().getPhoneno();
                    fbRef.child(application.getVendorUser().getPhoneno()).child("shops").child(shopkey).setValue(shopkey);

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("address", location);
                    map.put("category", category);
                    map.put("shop_name", shopname);
                    map.put("phoneno", phoneno);
                    map.put("email", email);
                    map.put("vendorid", application.getVendorUser().getPhoneno());
                    if(tempPicture != null) {
                        map.put("image", Commons.convertBitmapToBase64(tempPicture));
                        fbRef = new Firebase(Constants.FIREBASE_VENDOR).child(vendorkey).child("image");
                        fbRef.setValue(Commons.convertBitmapToBase64(tempPicture));
                    }

                    fbRef = new Firebase(Constants.FIREBASE_SHOPS);
                    fbRef.child(shopkey).setValue(map);
                    getShopInfo();
                    Commons.showToastMessage("Shops created", getApplicationContext());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    private void updateShopInfo()
    {
        String shopnamekey = application.getVendorUser().getShops().values().toArray()[0].toString();
        String vendorkey = application.getVendorUser().getPhoneno();
        String location = location_ET.getText().toString();
        String email = email_ET.getText().toString();
        String phoneno = telephone_ET.getText().toString();
        String category = category_Spinner.getSelectedItem().toString().toLowerCase();

        Commons.showProgressDialog(pd, "Shop profile", "Updating profile");

        new AsyncTask<String, Void, Void>(){
            @Override
            protected Void doInBackground(String... params) {
                // Wait for the camera starts up
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("address", params[0]);
                map.put("category", params[1]);
                map.put("phoneno", params[2]);
                map.put("email", params[3]);
                if(tempPicture != null) {
                    map.put("image", Commons.convertBitmapToBase64(tempPicture));
                    fbRef = new Firebase(Constants.FIREBASE_VENDOR).child(params[4]).child("image");
                    fbRef.setValue(Commons.convertBitmapToBase64(tempPicture));
                }

                fbRef = new Firebase(Constants.FIREBASE_SHOPS);
                fbRef.child(params[5]).updateChildren(map);
                SystemClock.sleep(1500);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                navDrawer.UpdateNavbarView();
                Commons.dismissProgressDialog(pd);
                getShopInfo();
                Commons.showToastMessage("Shops info udpated", getApplicationContext());
            }
        }.execute(location, category, phoneno, email, vendorkey, shopnamekey);
    }

    /**
     * Retrieve category from firebase and load it to category spinner
     */
    private void loadCategoryList()
    {
        // Check network connection
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            return;
        }
        Commons.showProgressDialog(pd, "Category list", "Loading category");
        fbRef = new Firebase(Constants.FIREBASE_CATEGORY);
        fbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> categoryList = new ArrayList<String>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String cat = ds.getValue().toString();
                    categoryList.add(Commons.firstLetterToUpper(cat));
                }

                // Create an ArrayAdapter using the string array and a default spinner layout
                adapter = new ArrayAdapter<String>(BusinessProfileActivity.this, android.R.layout.simple_spinner_item,
                        categoryList);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                category_Spinner.setAdapter(adapter);
                setEnableAllElements(true);
                Commons.dismissProgressDialog(pd);
                getShopInfo();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * To get shop info from firebase that belongs to a vendor
     */
    private void getShopInfo()
    {
        // Check network connection
        if(!Commons.isNetworkAvailable(getApplicationContext()))
        {
            Commons.showToastMessage("No internet connection", getApplicationContext());
            return;
        }
        Commons.showProgressDialog(pd, "Shop info", "Getting shop info");
        fbRef = new Firebase(Constants.FIREBASE_VENDOR);
        fbRef.child(user.getPhoneno()).child("shops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if no shop is created
                if (dataSnapshot.getValue() == null) {
                    Commons.dismissProgressDialog(pd);
                    final AlertDialog alertDialog = new AlertDialog.Builder(BusinessProfileActivity.this).create();
                    alertDialog.setTitle("No shop");
                    alertDialog.setMessage("You have not created a shop.");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    createBtn.setVisibility(View.VISIBLE);
                    updateBtn.setVisibility(View.INVISIBLE);
                    email_ET.setText(user.getEmail());
                    telephone_ET.setText(user.getPhoneno());
                } else {
                    createBtn.setVisibility(View.INVISIBLE);
                    updateBtn.setVisibility(View.VISIBLE);
                    shopName_ET.setKeyListener(null); // set shopname_ET uneditable
                    loadShopInfo(dataSnapshot.getChildren().iterator().next().getValue().toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * Get all information about the shop
     * @param shopkey the key of the shop
     */
    private void loadShopInfo(String shopkey)
    {
        Commons.showProgressDialog(pd, "Shop info", "Loading shop info");
        fbRef = new Firebase(Constants.FIREBASE_SHOPS);
        fbRef.child(shopkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shopName_ET.setText(dataSnapshot.child("shop_name").getValue().toString());
                location_ET.setText(dataSnapshot.child("address").getValue().toString());
                email_ET.setText(dataSnapshot.child("email").getValue().toString());
                telephone_ET.setText(dataSnapshot.child("phoneno").getValue().toString());
                String cat = dataSnapshot.child("category").getValue().toString();
                category_Spinner.setSelection(adapter.getPosition(Commons.firstLetterToUpper(cat)));
                if(dataSnapshot.child("image").getValue() != null)
                {
                    Bitmap image = Commons.convertBase64ToBitmap(dataSnapshot.child("image").getValue().toString());
                    profilePic.setImageBitmap(image);
                }
                Commons.dismissProgressDialog(pd);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
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
