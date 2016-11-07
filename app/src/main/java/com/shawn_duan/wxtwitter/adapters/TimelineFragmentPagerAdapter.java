package com.shawn_duan.wxtwitter.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.fragments.HomeTimelineFragment;
import com.shawn_duan.wxtwitter.fragments.MentionsTimelineFragment;

/**
 * Created by sduan on 11/3/16.
 */

public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Home", "Mentions"};

    private int[] imageResId = {
            R.drawable.ic_home_blue,
            R.drawable.ic_at_blue,
    };

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
                return new HomeTimelineFragment();
            case 1:
                return new MentionsTimelineFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, 80, 80);
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
