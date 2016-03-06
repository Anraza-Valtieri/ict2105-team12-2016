package com.example.chowdi.qremind;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LogoCoverPageActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Sleep for 2 seconds
        SystemClock.sleep(2000);

        Intent intent = new Intent(LogoCoverPageActivity.this, Login_RegisterActivity.class);
        startActivity(intent);
        finish();
    }

}
