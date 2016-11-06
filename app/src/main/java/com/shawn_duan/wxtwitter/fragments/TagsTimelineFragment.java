package com.shawn_duan.wxtwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.shawn_duan.wxtwitter.utils.Constants.TAG_KEY;

/**
 * Created by sduan on 11/5/16.
 */

public class TagsTimelineFragment extends TweetsListBaseFragment {

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
        Log.d(TAG, "populateTimeLine(), mTag, " + mTag + ", sinceId: " + sinceId + ", maxId: " + maxId + ", count: " + count);
        mClient.getSearchResult(mTag, sinceId, maxId, count, new JsonHttpResponseHandler() {
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
                int newTweetCount = response.length();
                int originalSize = mTweetList.size();

                if (addToBottom) {
                    mTweetList.addAll(Tweet.fromJSONArray(jsonArray));
                    mAdapter.notifyItemRangeInserted(originalSize + 1, newTweetCount);
                } else {
                    mTweetList.addAll(0, Tweet.fromJSONArray(jsonArray));
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
