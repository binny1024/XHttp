package com.dragon.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dragon.R;


public class TabLayoutActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String[] mTitle = new String[20];
    private String[] mData = new String[20];
    private PagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        initData();
        initView();
        bindViewPagerWithTab();
    }

    private void bindViewPagerWithTab() {
//        final TabLayout.TabLayoutOnPageChangeListener listener =
//                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout);
//        mViewPager.addOnPageChangeListener(listener);

//        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tl);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            mTitle[i] = "title" + i;
            mData[i] = "data" + i;
        }
        mAdapter = new PagerAdapter() {
            @Override
            public CharSequence getPageTitle(int position) {
                if (position >= mTitle.length) {
                    position %= mTitle.length;
                }
                return mTitle[position];
            }

            @Override
            public int getCount() {
//                return mData.length;
                return 100;
            }

            @Override
            public Object instantiateItem(View container, int position) {
                TextView tv = new TextView(TabLayoutActivity.this);
                tv.setTextSize(30.f);
                if (position >= mData.length) {
                    position = position % mData.length;
                }
                tv.setText(mData[position]);
                ((ViewPager) container).addView(tv);
                return tv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //Warning：不要在这里调用removeView
                ((ViewPager) container).removeView((View) object);

            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
    }
}
