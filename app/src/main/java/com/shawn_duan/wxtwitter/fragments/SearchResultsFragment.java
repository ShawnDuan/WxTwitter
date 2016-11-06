package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sduan on 11/6/16.
 */

public class SearchResultsFragment extends TimelineBaseFragment {
    private final static String TAG = SearchResultsFragment.class.getSimpleName();

    private String mQueryString;

    public static SearchResultsFragment newInstance(String query) {
        SearchResultsFragment resultsFragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        resultsFragment.setArguments(args);
        return resultsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQueryString = getArguments().getString("query");
        }
    }

    @Override
    protected void populateTimeline(long sinceId, final long maxId, int count) {
        mClient.getSearchResult(mQueryString, sinceId, maxId, count, new JsonHttpResponseHandler() {
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

    public void updateQuery(String query) {
        mQueryString = query;
        mTweetList.clear();
        populateTimeline(mNewestId, NOT_APPLICABLE, (int) NOT_APPLICABLE);
    }
}
