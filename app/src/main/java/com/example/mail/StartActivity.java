package com.example.mail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class StartActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 2000);
    }

    class splashhandler implements Runnable{
        public void run() {
            startActivity(new Intent(StartActivity.this,UserLoginActivity.class));
            StartActivity.this.finish();
        }
    }
}
