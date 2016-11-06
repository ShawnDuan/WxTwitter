package com.shawn_duan.wxtwitter.fragments;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sduan on 11/3/16.
 */

public class MentionsTimelineFragment extends TimelineBaseFragment {

    private final static String TAG = MentionsTimelineFragment.class.getSimpleName();

    @Override
    protected void populateTimeline(final long sinceId, final long maxId, final int count) {
        Log.d(TAG, "populateTimeline(), sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);
        mClient.getMentionsTimeline(sinceId, maxId, count, new JsonHttpResponseHandler() {
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
