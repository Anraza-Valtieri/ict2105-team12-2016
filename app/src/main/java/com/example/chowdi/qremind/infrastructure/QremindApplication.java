package com.example.chowdi.qremind.infrastructure;

import android.app.Application;

/**
 * Created by L on 3/26/2016.
 * Used as a singleton point
 * No constructor because may not be initialized yet by then.
 * onCreate would be a better place
 */
public class QremindApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
