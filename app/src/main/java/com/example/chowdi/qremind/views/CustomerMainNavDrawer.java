package com.example.chowdi.qremind.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chowdi.qremind.Customer.CustomerCurrentServing;
import com.example.chowdi.qremind.Customer.CustomerHomePageActivity;
import com.example.chowdi.qremind.Customer.CustomerProfilePageActivity;
import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by L on 3/27/2016.
 */
public class CustomerMainNavDrawer extends NavDrawer {

    private final TextView displayNameText;
    private final ImageView avaatarImage;

    public CustomerMainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new ActivityDrawerItem(CustomerHomePageActivity.class, "Home", null, R.drawable.ic_action_person, R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityDrawerItem(CustomerProfilePageActivity.class, "Profile", null, R.drawable.ic_action_group, R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityDrawerItem(CustomerCurrentServing.class, "MY Queue", null, R.drawable.ic_action_unread, R.id.include_main_nav_drawer_topItems));

        addItem(new BasicNavDrawerItem("Logout", null, R.drawable.ic_action_backspace, R.id.include_main_nav_drawer_bottomItems) {
            @Override
            public void onClick(View v) {
                Commons.logout(new Firebase(Constants.FIREBASE_MAIN), activity);
                Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show();
            }

        });

        displayNameText = (TextView) navDrawerView.findViewById(R.id.include_main_nav_drawer_displayName);
        avaatarImage = (ImageView)navDrawerView.findViewById(R.id.include_main_nav_drawer_avatar);
        UpdateNavbarView();
    }

    public void UpdateNavbarView()
    {
        if(activity.getQremindApplication().getCustomerUser() != null){
            displayNameText.setText(activity.getQremindApplication().getCustomerUser().getFirstname());
            avaatarImage.setImageBitmap(activity.getQremindApplication().getCustomerUser().getMyImage());
        }
    }
}
