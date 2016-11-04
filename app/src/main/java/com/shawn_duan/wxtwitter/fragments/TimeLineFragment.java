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

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.adapters.TweetsArrayAdapter;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.network.TwitterClient;
import com.shawn_duan.wxtwitter.rxbus.InsertNewTweetEvent;
import com.shawn_duan.wxtwitter.rxbus.RxBus;
import com.shawn_duan.wxtwitter.utils.DividerItemDecoration;
import com.shawn_duan.wxtwitter.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sduan on 10/29/16.
 */

public class TimeLineFragment extends Fragment {
    private final static String TAG = TimeLineFragment.class.getSimpleName();

    private TwitterClient mClient;

    @BindView(R.id.swipContainer)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rcTimeLine)
    RecyclerView mRecyclerView;
    private ArrayList<Tweet> mTweetList;
    private TweetsArrayAdapter mAdapter;
    private long mNewestId, mOldestId;
    private Unbinder unbinder;
    private Subscription mNewTweetSubscription;

    private final static int NORMAL_POPULATE_AMOUNT = 25;
    private final static long NOT_APPLICABLE = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = WxTwitterApplication.getRestClient();     // singleton client
        mTweetList = new ArrayList<>();

        mNewTweetSubscription = RxBus.getInstance().toObserverable(InsertNewTweetEvent.class)
                .subscribe(new Action1<InsertNewTweetEvent>() {
                    @Override
                    public void call(InsertNewTweetEvent insertNewTweetEvent) {
                        try {
                            Tweet tweet = insertNewTweetEvent.getTweet();
                            mTweetList.add(0, tweet);
                            mNewestId = tweet.getUid();
                            mAdapter.notifyItemInserted(0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

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
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNewTweetSubscription != null && !mNewTweetSubscription.isUnsubscribed()) {
            mNewTweetSubscription.unsubscribe();
        }
    }

    // if count is -1 or 0, populate as much as possible in the range of sinceId to maxId.
    private void populateTimeline(long sinceId, final long maxId, int count) {

        Log.d(TAG, "populateTimeLine(), sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);
        mClient.getHomeTimeLine(sinceId, maxId, count, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Deserialize Json
                // Create models
                // Load the model
                boolean addToBottom = (maxId != NOT_APPLICABLE);
                int newTweetCount = response.length();
                int originalSize = mTweetList.size();

                if (addToBottom) {
                    mTweetList.addAll(Tweet.fromJSONArray(response));
                    mAdapter.notifyItemRangeInserted(originalSize + 1, newTweetCount);
                } else {
                    mTweetList.addAll(0, Tweet.fromJSONArray(response));
                    mAdapter.notifyItemRangeInserted(0, newTweetCount);
                }

                // update max/since id based on the current TweetList
                mNewestId = mTweetList.get(0).getUid();
                mOldestId = mTweetList.get(mTweetList.size() - 1).getUid();

                if (!addToBottom) {
                    mRecyclerView.smoothScrollToPosition(0);
                }

                Log.d(TAG, "Amount of new tweets added into timeline: " + newTweetCount);

                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 429) {
                    Toast.makeText(getActivity(),
                            "Request number meets the limit, please wait for 15mins before retry.",
                            Toast.LENGTH_SHORT).show();
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void populateTimeline() {
        populateTimeline(0, 0, 0);
    }

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
