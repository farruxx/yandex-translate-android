package com.farruxx.yandextranslator.view;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.farruxx.yandextranslator.R;
import com.farruxx.yandextranslator.ui.CustomVerticalViewPager;
import com.farruxx.yandextranslator.ui.CustomViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by Farruxx on 23.04.2017.
 */

public class StartActivity extends AppCompatActivity{
    @BindView(R.id.tabbar)
    BottomBar bottomBar;

    @Nullable
    @BindView(R.id.pager)
    CustomViewPager pager;

    @Nullable
    @BindView(R.id.pagerLand)
    CustomVerticalViewPager pagerLand;
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment result;
                switch (position){
                    case 0:
                        result = new TranslateFragment();
                        break;
                    case 1:
                        result = new HistoryFragment();
                        break;
                    case 2:
                        result = new SettingsFragment();
                        break;
                    default:
                        result = new Fragment();
                }
                return result;
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        if(pager != null) {
            bottomBar.setOnTabSelectListener(tabId -> pager.setCurrentItem(bottomBar.findPositionForTabWithId(tabId)));
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(3);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    bottomBar.selectTabAtPosition(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        if(pagerLand != null){
            bottomBar.setOnTabSelectListener(tabId -> pagerLand.setCurrentItem(bottomBar.findPositionForTabWithId(tabId)));
            pagerLand.setAdapter(adapter);
            pagerLand.setOffscreenPageLimit(3);
            pagerLand.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    bottomBar.selectTabAtPosition(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }
}
