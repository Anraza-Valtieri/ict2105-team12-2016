package com.example.chowdi.qremind.views;

import android.content.Intent;
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
import com.firebase.client.Firebase;

/**
 * Created by L on 3/27/2016.
 */
public class CustomerMainNavDrawer extends NavDrawer {

    private final TextView displayNameText;
    private final ImageView avatarImage;

    public CustomerMainNavDrawer(final BaseActivity activity) {
        super(activity);

        addItem(new ActivityDrawerItem(CustomerHomePageActivity.class, "Home", null, R.drawable.ic_action_person, R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityDrawerItem(CustomerProfilePageActivity.class, "Profile", null, R.drawable.ic_action_group, R.id.include_main_nav_drawer_topItems));

        addItem(new ActivityDrawerItem(CustomerCurrentServing.class, "MY Queue", null, R.drawable.ic_action_unread, R.id.include_main_nav_drawer_topItems){
            @Override
            public void onClick(View v) {
                if(activity.getQremindApplication().getCustomerUser().getCurrent_queue() == null)
                {
                    Commons.showToastMessage("You are not in any queue", activity.getApplicationContext());
                    return;
                }

                super.onClick(v);

                //ANIMATIONS
                activity.fadeOut(new BaseActivity.FadeOutListener() {
                    @Override
                    public void onFadeOutEnd() {
                        activity.startActivity(new Intent(activity, CustomerCurrentServing.class));
                        activity.finish();
                    }
                });
            }
        });

        addItem(new BasicNavDrawerItem("Logout", null, R.drawable.ic_action_backspace, R.id.include_main_nav_drawer_bottomItems) {
            @Override
            public void onClick(View v) {
                Commons.logout(new Firebase(Constants.FIREBASE_MAIN), activity);
                Commons.showToastMessage("Logged out", activity.getApplicationContext());
            }
        });

        displayNameText = (TextView) navDrawerView.findViewById(R.id.include_main_nav_drawer_displayName);
        avatarImage = (ImageView)navDrawerView.findViewById(R.id.include_main_nav_drawer_avatar);
        UpdateNavbarView();
    }

    public void UpdateNavbarView()
    {
        if(activity.getQremindApplication().getCustomerUser() != null){
            displayNameText.setText(activity.getQremindApplication().getCustomerUser().getFirstname());
            avatarImage.setImageBitmap(activity.getQremindApplication().getCustomerUser().getMyImage());
        }
    }
}
