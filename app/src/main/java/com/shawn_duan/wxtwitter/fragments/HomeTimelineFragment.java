package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.rxbus.InsertNewTweetEvent;
import com.shawn_duan.wxtwitter.rxbus.RxBus;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by sduan on 10/29/16.
 */

public class HomeTimelineFragment extends TimelineBaseFragment {
    private final static String TAG = HomeTimelineFragment.class.getSimpleName();


    private Subscription mNewTweetSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewTweetSubscription = RxBus.getInstance().toObserverable(InsertNewTweetEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<InsertNewTweetEvent>() {
                    @Override
                    public void call(InsertNewTweetEvent insertNewTweetEvent) {
                        try {
                            Tweet tweet = insertNewTweetEvent.getTweet();
                            mTweetList.add(0, tweet);
                            mNewestId = tweet.getUid();
                            mAdapter.notifyItemInserted(0);
                            mRecyclerView.smoothScrollToPosition(0);
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

        Log.d(TAG, "populateTimeline(), sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);
        mClient.getHomeTimeline(sinceId, maxId, count, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                boolean addToBottom = (maxId != NOT_APPLICABLE);
                handleResponseArray(response, addToBottom);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                handleError(statusCode);
            }
        });
    }

}
