package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.adapters.TimelineFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sduan on 11/3/16.
 */

public class TabAndPagerFragment extends Fragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_and_pager, container, false);
        ButterKnife.bind(this, view);

        mViewPager.setAdapter(new TimelineFragmentPagerAdapter(getActivity().getSupportFragmentManager(), getActivity()));

        // Give the TabLayout the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }
}
