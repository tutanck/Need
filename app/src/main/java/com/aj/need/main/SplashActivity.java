package com.aj.need.main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.aj.need.R;
import com.aj.need.services._GooglePlayService;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.aj.need.tools.utils.__.showShortToast;


/**
 * Created by joan on 01/10/2017.
 */

public class SplashActivity extends Activity {

    private final static String TAG = "SplashAct";

    private static int SPLASH_TIME_OUT = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }


    @Override
    protected void onResume() {
        super.onResume();
        _GooglePlayService.checkPlayServices(this);
        begin();
    }


    private void begin() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, App.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) // Got last known location. In some rare situations this can be null. Dt override old location if current is null
                        ((App) getApplication()).setLastLocalKnownLocation(location);
                } else
                    Log.d(TAG, "getLastLocation", task.getException());


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
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case App.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    begin();
                else {
                    showShortToast(this, getString(R.string.location_required));
                    finish();
                }
                return;
            }
        }
    }
}