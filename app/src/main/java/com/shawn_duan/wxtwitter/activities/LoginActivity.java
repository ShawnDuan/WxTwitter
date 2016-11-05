package com.shawn_duan.wxtwitter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.models.User;
import com.shawn_duan.wxtwitter.network.TwitterClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.shawn_duan.wxtwitter.utils.Constants.USER_NAME_PREF_KEY;
import static com.shawn_duan.wxtwitter.utils.Constants.USER_PROFILE_IMAGE_URL_PREF_KEY;
import static com.shawn_duan.wxtwitter.utils.Constants.USER_SCREEN_NAME_PREF_KEY;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	private final static String TAG = LoginActivity.class.getSimpleName();

	private TwitterClient mClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		// Intent i = new Intent(this, PhotosActivity.class);
		// startActivity(i);

		final SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
		String username = defaultPref.getString(USER_NAME_PREF_KEY, null);
		if (username == null) {
			mClient = WxTwitterApplication.getRestClient();
			mClient.getSelfUserInfo(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					User user = User.fromJSONObject(response);
					SharedPreferences.Editor editor = defaultPref.edit();
					editor.putString(USER_NAME_PREF_KEY, user.getName());
					editor.putString(USER_SCREEN_NAME_PREF_KEY, user.getScreenName());
					editor.putString(USER_PROFILE_IMAGE_URL_PREF_KEY, user.getProfileImageUrl());
					editor.apply();
					Log.d(TAG, "Stored sharedPreference.");

					startMainActivity();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}
			});
		} else {
			startMainActivity();
		}
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

	private void startMainActivity() {
		Toast.makeText(LoginActivity.this, "Success log in.", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
	}

}
