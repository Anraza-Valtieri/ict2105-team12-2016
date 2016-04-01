package com.example.chowdi.qremind.Customer;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;

/**
 * Created by lotus on 4/1/2016.
 */
public class ViewVendorActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_view_vendor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
