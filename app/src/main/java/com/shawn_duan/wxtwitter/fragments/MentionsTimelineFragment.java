package com.shawn_duan.wxtwitter.fragments;

import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sduan on 11/3/16.
 */

public class MentionsTimelineFragment extends TweetsListBaseFragment {

    private final static String TAG = MentionsTimelineFragment.class.getSimpleName();

    @Override
    protected void populateTimeline(final long sinceId, final long maxId, final int count) {
        Log.d(TAG, "populateTimeLine(), sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);
        mClient.getMentionsTimeLine(sinceId, maxId, count, new JsonHttpResponseHandler() {
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
