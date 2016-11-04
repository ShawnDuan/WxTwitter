package com.shawn_duan.wxtwitter.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shawn_duan.wxtwitter.fragments.TimeLineFragment;

/**
 * Created by sduan on 11/3/16.
 */

public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "HomeTimeline", "Mentioned"};
    final int PAGE_COUNT = tabTitles.length;

    private Context context;

    public TimelineFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TimeLineFragment();
            case 1:
                return new TimeLineFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
