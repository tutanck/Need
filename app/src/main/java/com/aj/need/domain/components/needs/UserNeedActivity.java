package com.aj.need.domain.components.needs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aj.need.R;


/**
 * Created by joan on 04/10/2017.
 */
public class UserNeedActivity extends AppCompatActivity {

    private final static String TAG = "UserNeedAct", NEED_ID = "NEED_ID", NEED_TITLE = "NEED_TITLE",
            APPLICANT_ID = "APPLICANT_ID", APPLICANT_NAME = "APPLICANT_NAME";

    private String needID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_ad);

        Log.d("_UserNeedActivityExtra", getIntent().getExtras().toString());

        needID = getIntent().getStringExtra(NEED_ID);
        String needTitle = getIntent().getStringExtra(NEED_TITLE);


        /*THESE INSTRUCTIONS MUST BE AFTER SETTING needID CAUSE THEY USE ITS VALUE*/

        TextView needTitleTV = findViewById(R.id.needTitleTV);
        needTitleTV.setText(needTitle);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.user_need_results_viewpager);
        viewPager.setAdapter(
                new PagerAdapter(
                        getSupportFragmentManager()
                        , UserNeedActivity.this
                )
        );

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.user_need_results_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab_open_need_save);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNeedSaveActivity.start(UserNeedActivity.this, needID, true);
            }
        });
    }


    public static void start(Context context, String needID, String needTitle) {
        Intent intent = new Intent(context, UserNeedActivity.class);
        intent.putExtra(NEED_ID, needID);
        intent.putExtra(NEED_TITLE, needTitle);
        context.startActivity(intent);
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserNeedActivity.class));
    }

    private /*static*/ class PagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        private String TAB_TITLES[] = new String[]{"PROPOSITIONS", "PROFILS TROUVES"};

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PokesFragment.newInstance(needID);
                case 1:
                    return SearchFragment.newInstance("j"); //// TODO: 14/11/2017
                default:
                    throw new RuntimeException(TAG + "/PagerAdapter::getItem : unknown top level tab menu");
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

}