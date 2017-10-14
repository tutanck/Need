package com.aj.need.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aj.need.R;

import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.aj.need.domain.components.needs.userneeds.UserNeedsFragment;
import com.aj.need.domain.components.profile.ProfileFragment;
import com.aj.need.tools.oths.PageFragment;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;


public class MainActivity extends AppCompatActivity {

    private int mAvailability = Avail.OFFLINE;

    private FirebaseAuth.AuthStateListener authListener;

    ListenerRegistration profileRegistration;

    static void start(final Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    LoginActivity.start(MainActivity.this);
            }
        };

        resetAvailabilityBtn(mAvailability);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_main);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new PagerAdapter(
                        getSupportFragmentManager()
                        , MainActivity.this
                )
        );

        viewPager.setOffscreenPageLimit(3);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    protected void onStart() {
        super.onStart();


        USERS.getCurrentUserRef().update(USERS.availabilityKey, Avail.AVAILABLE);

        profileRegistration = USERS.getCurrentUserRef().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e == null && snapshot != null && snapshot.exists()) {
                    Log.d("AvailabilityListener: ", "Current data: " + snapshot.getData().toString());
                    resetAvailabilityBtn(((Long) snapshot.getData().get(USERS.availabilityKey)).intValue());
                }
            }
        });

        IO.auth.addAuthStateListener(authListener);
    }


    @Override
    public void onStop() {
        super.onStop();

        if (authListener != null)
            IO.auth.removeAuthStateListener(authListener);

        profileRegistration.remove();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Changement de disponibilité");

                if (mAvailability == Avail.AVAILABLE)
                    builder.setMessage(getString(R.string.avail_to_busy_warning));
                else
                    builder.setMessage(getString(R.string.avail_to_available_warning));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        USERS.getCurrentUserRef().update(USERS.availabilityKey, Avail.nextStatus(mAvailability));
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
                break;

            case R.id.action_settings:
                USERS.getCurrentUserRef().update(USERS.availabilityKey, Avail.OFFLINE)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("signOut", "signOut");
                                IO.auth.signOut();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                __.showShortToast(MainActivity.this, "Impossible de se deconnecter");//// TODO: 13/10/2017
                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context context;

        private String TAB_TITLES[] = new String[]{"ANNONCES", "PROFIL", "BESOINS", "MESSAGES"};

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PageFragment.newInstance(position);//AdsFragment.newInstance();
                case 1:
                    return ProfileFragment.newInstance(IO.auth.getCurrentUser().getUid(), true);
                case 2:
                    return UserNeedsFragment.newInstance();
                case 3:
                    return PageFragment.newInstance(position);//ConversationsFragment.newInstance();
                default:
                    throw new RuntimeException("Unknown top level tab menu");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }


    private void resetAvailabilityBtn(int availability) {
        mAvailability = availability;
        getSupportActionBar().setHomeAsUpIndicator(Avail.getDrawable(mAvailability));
    }


}
