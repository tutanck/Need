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
import android.view.View;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.domain.components.needs.main.UserNeed;


/**
 * Created by joan on 04/10/2017.
 */
public class UserNeedAdActivity extends AppCompatActivity {

    private final static String USER_NEED = "USER_NEED";

    private UserNeed mUserNeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_ad);

        mUserNeed = (UserNeed) getIntent().getSerializableExtra(USER_NEED);

        TextView needTitleTV = (TextView) findViewById(R.id.needTitleTV);
        needTitleTV.setText(mUserNeed.getTitle());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.user_need_results_viewpager);
        viewPager.setAdapter(
                new PagerAdapter(
                        getSupportFragmentManager()
                        , UserNeedAdActivity.this
                )
        );

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.user_need_results_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_open_need_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNeedSaveActivity.start(UserNeedAdActivity.this, mUserNeed.get_id(), true);
            }
        });
    }


    public static void start(Context context, UserNeed userNeed) {
        Intent intent = new Intent(context, UserNeedAdActivity.class);
        intent.putExtra(USER_NEED, userNeed);
        context.startActivity(intent);
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserNeedAdActivity.class));
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        private String TAB_TITLES[] = new String[]{"DISPONIBLES NOW", "POKES RECUES"};

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return NeedProfilesFragment.newInstance();
                case 1:
                    return NeedProfilesFragment.newInstance();
                default:
                    throw new RuntimeException("PagerAdapter's top level tab menu");
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