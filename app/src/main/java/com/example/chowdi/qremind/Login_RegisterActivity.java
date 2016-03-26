package com.example.chowdi.qremind;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

public class Login_RegisterActivity extends AppCompatActivity{

    // Variables for all UI elements
    private Button loginBtn;
    private Button createAccBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Find and assign the UI element by R.id
        loginBtn = (Button) findViewById(R.id.loginbtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(Login_RegisterActivity.this, LoginActivity.class);
                startActivity(view);
            }
        });
        createAccBtn = (Button) findViewById(R.id.createacctbtn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(Login_RegisterActivity.this, Register_Activity.class);
                startActivity(view);
            }
        });
    }
}
