package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.network.TwitterClient;
import com.shawn_duan.wxtwitter.rxbus.InsertNewTweetEvent;
import com.shawn_duan.wxtwitter.rxbus.RxBus;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sduan on 10/29/16.
 */

public class HomeTimelineFragment extends TweetsListBaseFragment {
    private final static String TAG = HomeTimelineFragment.class.getSimpleName();


    private Subscription mNewTweetSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNewTweetSubscription != null && !mNewTweetSubscription.isUnsubscribed()) {
            mNewTweetSubscription.unsubscribe();
        }
    }

    // if count is -1 or 0, populate as much as possible in the range of sinceId to maxId.
    protected void populateTimeline(final long sinceId, final long maxId, final int count) {

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

}
