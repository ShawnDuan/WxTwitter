package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.shawn_duan.wxtwitter.utils.Constants.SCREEN_NAME_KEY;

/**
 * Created by sduan on 11/3/16.
 */

public class UserTimelineFragment extends TimelineBaseFragment {
    private final static String TAG = UserTimelineFragment.class.getSimpleName();

    private String mScreenName;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(SCREEN_NAME_KEY, screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenName = getArguments().getString(SCREEN_NAME_KEY);
    }

    @Override
    protected void populateTimeline(final long sinceId, final long maxId, final int count) {
        Log.d(TAG, "populateTimeline(), screenName: " + mScreenName + ", sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);

        mClient.getUserTimeline(mScreenName, sinceId, maxId, count, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Deserialize Json
                // Create models
                // Load the model
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
