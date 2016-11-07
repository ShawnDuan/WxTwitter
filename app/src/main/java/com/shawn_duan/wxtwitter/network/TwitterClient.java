package com.shawn_duan.wxtwitter.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import static com.shawn_duan.wxtwitter.utils.Constants.USER_SCREEN_NAME_PREF_KEY;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	private final static String TAG = TwitterClient.class.getSimpleName();

	private Context mContext;
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = "ol2xErDjBT05EBvC0hIo1t1GT";
	public static final String REST_CONSUMER_SECRET = "xJj56MtUQFbfQ0XbT4FT8dDJ4hExocJ3hi1NkwM84MO8NQkHYV"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://wxtwitter"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
		mContext = context;
	}

	public void getHomeTimeline(long sinceId, long maxId, int count, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Specify the params
		RequestParams params = new RequestParams();

		if (sinceId > 0) {
			params.put("since_id", sinceId);
		}
		if (maxId > 0) {
			params.put("max_id", maxId - 1);
		}
		// unlimited if count = -1
		if (count > 0) {
			params.put("count", count);
		}
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(long sinceId, long maxId, int count, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Specify the params
		RequestParams params = new RequestParams();

		if (sinceId > 0) {
			params.put("since_id", sinceId);
		}
		if (maxId > 0) {
			params.put("max_id", maxId - 1);
		}
		// unlimited if count = -1
		if (count > 0) {
			params.put("count", count);
		}
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	public void getUserTimeline(String screenName, long sinceId, long maxId, int count, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Specify the params
		RequestParams params = new RequestParams();

		if (screenName != null && screenName.length() > 0) {
			params.put("screen_name", screenName);
		}
		if (sinceId > 0) {
			params.put("since_id", sinceId);
		}
		if (maxId > 0) {
			params.put("max_id", maxId - 1);
		}
		// unlimited if count = -1
		if (count > 0) {
			params.put("count", count);
		}
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	public void composeTweet(String status, long replyToTweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Specify the params
		RequestParams params = new RequestParams();
		params.put("status", status);
		if (replyToTweetId > 0) {
			params.put("in_reply_to_status_id", replyToTweetId);
		}
		// Execute the request
		getClient().post(apiUrl, params, handler);
	}

	public void getUserInfo(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		if (screenName == null || screenName.length() == 0) {
			SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(mContext);
			screenName = defaultPref.getString(USER_SCREEN_NAME_PREF_KEY, "");
		}
		Log.d(TAG, "screenName: " + screenName);
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);

	}

	public void getSelfUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	public void getSearchResult(String query, long sinceId, long maxId, int count, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		if (sinceId > 0) {
			params.put("since_id", sinceId);
		}
		if (maxId > 0) {
			params.put("max_id", maxId - 1);
		}
		// unlimited if count = -1
		if (count > 0) {
			params.put("count", count);
		}
		if (query == null || query.length() <= 0) {
			return;
		} else {
			params.put("q", query);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void setTweetFavorite(boolean setTrue, long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = setTrue ? getApiUrl("favorites/create.json") : getApiUrl("favorites/destroy.json");
		// Specify the params
		RequestParams params = new RequestParams();
		if (tweetId > 0) {
			params.put("id", tweetId);
		}
		// Execute the request
		getClient().post(apiUrl, params, handler);
	}

	public void setTweetRetweet(boolean setTrue, long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = setTrue ? getApiUrl("statuses/retweet.json") : getApiUrl("statuses/unretweet.json");
		RequestParams params = new RequestParams();
		if (tweetId > 0) {
			params.put("id", tweetId);
		}
		getClient().post(apiUrl, params, handler);
	}

}
