package com.example.chowdi.qremind.activities;

import android.os.Bundle;

/**
 * Created by lotus on 3/30/2016.
 */
public abstract class BaseAuthenticatedActivity extends BaseActivity {
    @Override
    protected final void onCreate(Bundle savedState) {
        super.onCreate(savedState);

//        if(application.getAuth().getUser().isLoggedIn()){
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//            return;
//        }

        onQremindCreate(savedState);
    }

    protected  abstract void onQremindCreate(Bundle savedState);
    }

