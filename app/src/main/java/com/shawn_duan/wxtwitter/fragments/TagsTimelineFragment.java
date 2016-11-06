package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.shawn_duan.wxtwitter.utils.Constants.TAG_KEY;

/**
 * Created by sduan on 11/5/16.
 */

public class TagsTimelineFragment extends TimelineBaseFragment {

    private final static String TAG = TagsTimelineFragment.class.getSimpleName();

    private String mTag;

    public static TagsTimelineFragment newInstance(String tag) {
        TagsTimelineFragment tagsTimelineFragment = new TagsTimelineFragment();
        Bundle args = new Bundle();
        args.putString(TAG_KEY, tag);
        tagsTimelineFragment.setArguments(args);
        return tagsTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = getArguments().getString(TAG_KEY);
    }

    @Override
    protected void populateTimeline(final long sinceId, final long maxId, final int count) {
        Log.d(TAG, "populateTimeline(), mTag, " + mTag + ", sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);
        mClient.getSearchResult(mTag, sinceId, maxId, count, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("statuses");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                boolean addToBottom = (maxId != NOT_APPLICABLE);
                handleResponseArray(jsonArray, addToBottom);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                handleError(statusCode);
            }
        });
    }
}
