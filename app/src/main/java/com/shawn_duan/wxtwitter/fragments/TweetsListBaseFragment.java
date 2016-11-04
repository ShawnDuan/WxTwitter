package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.adapters.TweetsArrayAdapter;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.network.TwitterClient;
import com.shawn_duan.wxtwitter.utils.DividerItemDecoration;
import com.shawn_duan.wxtwitter.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sduan on 11/3/16.
 */

public abstract class TweetsListBaseFragment extends Fragment {
    private final static String TAG = TweetsListBaseFragment.class.getSimpleName();

    final static int NORMAL_POPULATE_AMOUNT = 25;
    final static long NOT_APPLICABLE = 0;

    @BindView(R.id.swipContainer)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rcTimeLine)
    RecyclerView mRecyclerView;

    TwitterClient mClient;
    ArrayList<Tweet> mTweetList;
    TweetsArrayAdapter mAdapter;
    long mNewestId, mOldestId;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = WxTwitterApplication.getRestClient();     // singleton client
        mTweetList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupRecyclerView();

        // only do auto-refresh at the first time.
        if (mTweetList.size() == 0) {
            populateTimeline(mNewestId, NOT_APPLICABLE, (int) NOT_APPLICABLE);
        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void appendTweets(List<Tweet> tweets) {
        // add tweets to the adapter
    }

    // Abstract method to be overridden
    protected abstract void populateTimeline(final long sinceId, final long maxId, final int count);


    private void setupRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(mNewestId, NOT_APPLICABLE, (int) NOT_APPLICABLE);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TweetsArrayAdapter(getActivity(), mTweetList);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore()");
                populateTimeline(NOT_APPLICABLE, mOldestId, NORMAL_POPULATE_AMOUNT);
            }
        });
    }

}