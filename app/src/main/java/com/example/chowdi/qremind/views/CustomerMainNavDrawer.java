package com.example.chowdi.qremind.views;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chowdi.qremind.Customer.CustomerCurrentServing;
import com.example.chowdi.qremind.Customer.CustomerHomePageActivity;
import com.example.chowdi.qremind.Customer.CustomerProfilePageActivity;
import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.infrastructure.Customer;
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

        addItem(new ActivityDrawerItem(CustomerHomePageActivity.class, "Home", null, R.drawable.ic_home_black_24dp, R.id.include_main_nav_drawer_topItems));
        addItem(new ActivityDrawerItem(CustomerProfilePageActivity.class, "Profile", null, R.drawable.ic_account_box_black_24dp, R.id.include_main_nav_drawer_topItems));

        addItem(new ActivityDrawerItem(CustomerCurrentServing.class, "MY Queue", null, R.drawable.ic_action_group, R.id.include_main_nav_drawer_topItems){
            @Override
            public void onClick(View v) {
                if(activity.getQremindApplication().getCustomerUser().getCurrent_queue() == null)
                {
                    Commons.showToastMessage("You are not in any queue", activity);
                }
            }
        });

        addItem(new BasicNavDrawerItem("Logout", null, R.drawable.ic_backspace_black_24dp, R.id.include_main_nav_drawer_bottomItems) {
            @Override
            public void onClick(View v) {
                Commons.logout(new Firebase(Constants.FIREBASE_MAIN), activity);
                Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show();
            }
        });

        displayNameText = (TextView) navDrawerView.findViewById(R.id.include_main_nav_drawer_displayName);
        avatarImage = (ImageView)navDrawerView.findViewById(R.id.include_main_nav_drawer_avatar);
        UpdateNavbarView();
    }

    @Override
    public void UpdateNavbarView()
    {
        Customer customer = activity.getQremindApplication().getCustomerUser();
        if(activity.getQremindApplication().getCustomerUser() != null){
            displayNameText.setText(activity.getQremindApplication().getCustomerUser().getFirstname());
            if(customer.getMyImage() == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                    avatarImage.setImageDrawable(activity.getDrawable(R.drawable.unknown_person));
                else
                    avatarImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.unknown_person));
            }
            else
                avatarImage.setImageBitmap(customer.getMyImage());
        }
    }
}
