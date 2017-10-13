package com.aj.need.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.aj.need.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by joan on 01/10/2017.
 */

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getCurrentUser() != null)
                    MainActivity.start(SplashActivity.this);
                else
                    LoginActivity.start(SplashActivity.this);
            }
        }, SPLASH_TIME_OUT);
    }

}