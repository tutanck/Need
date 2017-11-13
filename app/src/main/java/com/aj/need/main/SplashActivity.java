package com.aj.need.main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.aj.need.R;
import com.aj.need.tools.utils.__;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by joan on 01/10/2017.
 */

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1000;

    private App app;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        app = (App) getApplication();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
    }


    @Override
    protected void onStart() {
        super.onStart();

        /* todo ggl play services  : @see https://firebase.google.com/docs/cloud-messaging/android/client
        todo : consider doing this in the splash activity iof
        Apps that rely on the Play Services SDK should always check the device for a compatible Google Play services APK before accessing Google Play services features. It is recommended to do this in two places: in the main activity's onCreate() method, and in its onResume() method. The check in onCreate() ensures that the app can't be used without a successful check. The check in onResume() ensures that if the user returns to the running app through some other means, such as through the back button, the check is still performed.
        If the device doesn't have a compatible version of Google Play services, your app can call GoogleApiAvailability.makeGooglePlayServicesAvailable() to allow users to download Google Play services from the Play Store. */

        start();
    }


    private void start() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, App.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        //// TODO: 13/11/2017 sync (add a guard bf continuing : bug pos not avail)
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                    app.setLocation(location); // Got last known location. In some rare situations this can be null.
            }
        });


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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case App.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    start();
                else {
                    __.showShortToast(this, getString(R.string.location_required));
                    finish();
                }
                return;
            }
        }
    }
}