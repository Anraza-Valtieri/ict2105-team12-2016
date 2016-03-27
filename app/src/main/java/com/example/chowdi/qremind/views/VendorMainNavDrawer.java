package com.example.chowdi.qremind.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.Vendor.BusinessProfileActivity;
import com.example.chowdi.qremind.Vendor.VendorDashBoardActivity;
import com.example.chowdi.qremind.activities.BaseActivity;

/**
 * Created by L on 3/27/2016.
 */
public class VendorMainNavDrawer extends NavDrawer{
    private final TextView displayNameText;
    private final ImageView avaatarImage;

    public VendorMainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new NavDrawer.ActivityDrawerItem(VendorDashBoardActivity.class, "Home", null, R.drawable.ic_action_person, R.id.include_main_nav_drawer_topItems));
        addItem(new NavDrawer.ActivityDrawerItem(BusinessProfileActivity.class, "Profile", null, R.drawable.ic_action_person, R.id.include_main_nav_drawer_topItems));

        addItem(new NavDrawer.BasicNavDrawerItem("Logout",null, R.drawable.ic_action_backspace,R.id.include_main_nav_drawer_bottomItems) {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show();
            }

        });

        displayNameText = (TextView) navDrawerView.findViewById(R.id.include_main_nav_drawer_displayName);
        avaatarImage = (ImageView)navDrawerView.findViewById(R.id.include_main_nav_drawer_avatar);

        //TODO: change avatarImage to avatarURL from user
    }
}
