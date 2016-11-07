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
import android.widget.Toast;

import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.adapters.TweetsArrayAdapter;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.network.TwitterClient;
import com.shawn_duan.wxtwitter.utils.DividerItemDecoration;
import com.shawn_duan.wxtwitter.utils.EndlessRecyclerViewScrollListener;
import com.shawn_duan.wxtwitter.utils.NetworkUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sduan on 11/3/16.
 */

public abstract class TimelineBaseFragment extends Fragment {
    private final static String TAG = TimelineBaseFragment.class.getSimpleName();

    final static int NORMAL_POPULATE_AMOUNT = 25;
    final static long NOT_APPLICABLE = 0;

    @BindView(R.id.swipContainer)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rcTimeline)
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

    // Abstract method to be overridden
    protected abstract void populateTimeline(final long sinceId, final long maxId, final int count);


    private void setupRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(mNewestId, NOT_APPLICABLE, (int) NOT_APPLICABLE);
                if (!NetworkUtils.isNetworkAvailable(getActivity())) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
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

    protected void handleResponseArray(JSONArray jsonArray, boolean addToBottom) {
        int newTweetCount = jsonArray.length();
        int originalSize = mTweetList.size();

        if (addToBottom) {
            mTweetList.addAll(Tweet.fromJSONArray(jsonArray));
            mAdapter.notifyItemRangeInserted(originalSize + 1, newTweetCount);
        } else {
            mTweetList.addAll(0, Tweet.fromJSONArray(jsonArray));
            mAdapter.notifyItemRangeInserted(0, newTweetCount);
        }

        // update max/since id based on the current TweetList
        if (mTweetList.size() > 0) {
            mNewestId = mTweetList.get(0).getUid();
            mOldestId = mTweetList.get(mTweetList.size() - 1).getUid();
        } else {
            mNewestId = NOT_APPLICABLE;
            mOldestId = NOT_APPLICABLE;
        }

        if (!addToBottom) {
            mRecyclerView.smoothScrollToPosition(0);
        }

        Log.d(TAG, "Amount of new tweets added into timeline: " + newTweetCount);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    protected void handleError(int statusCode) {
        if (statusCode == 429) {
            Toast.makeText(getActivity(),
                    "Request number meets the limit, please wait for 15mins before retry.",
                    Toast.LENGTH_SHORT).show();
        }
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

}