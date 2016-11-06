package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sduan on 11/6/16.
 */

public class SearchResultsFragment extends TweetsListBaseFragment {
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
                // Deserialize Json
                // Create models
                // Load the model
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("statuses");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                boolean addToBottom = (maxId != NOT_APPLICABLE);
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

    public void updateQuery(String query) {
        mQueryString = query;
        mTweetList.clear();
        populateTimeline(mNewestId, NOT_APPLICABLE, (int) NOT_APPLICABLE);
//        populateTimeline(mNewestId, mOldestId, (int) NOT_APPLICABLE);
    }
}
