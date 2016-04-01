package com.example.chowdi.qremind.views;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.Vendor.BusinessProfileActivity;
import com.example.chowdi.qremind.Vendor.VendorDashBoardActivity;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Vendor;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.Firebase;

/**
 * Created by L on 3/27/2016.
 */
public class VendorMainNavDrawer extends NavDrawer{
    private final TextView displayNameText;
    private final ImageView avatarImage;

    public VendorMainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new NavDrawer.ActivityDrawerItem(VendorDashBoardActivity.class, "Home", null, R.drawable.ic_home_black_24dp, R.id.include_main_nav_drawer_topItems));
        addItem(new NavDrawer.ActivityDrawerItem(BusinessProfileActivity.class, "Shop Profile", null, R.drawable.ic_account_box_black_24dp, R.id.include_main_nav_drawer_topItems));

        addItem(new NavDrawer.BasicNavDrawerItem("Logout",null, R.drawable.ic_backspace_black_24dp,R.id.include_main_nav_drawer_bottomItems) {
            @Override
            public void onClick(View v) {
                Commons.logout(new Firebase(Constants.FIREBASE_MAIN), activity);
                Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show();
            }

        });

        displayNameText = (TextView) navDrawerView.findViewById(R.id.include_main_nav_drawer_displayName);
        avatarImage = (ImageView)navDrawerView.findViewById(R.id.include_main_nav_drawer_avatar);

        //TODO: change avatarImage to avatarURL from user
        UpdateNavbarView();
    }

    @Override
    public void UpdateNavbarView()
    {
        Vendor vendor = activity.getQremindApplication().getVendorUser();
        if(activity.getQremindApplication().getVendorUser() != null){
            displayNameText.setText(activity.getQremindApplication().getVendorUser().getFirstname());
            if(vendor.getMyImage() == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                    avatarImage.setImageDrawable(activity.getDrawable(R.drawable.unknown_building));
                else
                    avatarImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.unknown_building));
            }
            else
                avatarImage.setImageBitmap(vendor.getMyImage());
        }
    }
}
